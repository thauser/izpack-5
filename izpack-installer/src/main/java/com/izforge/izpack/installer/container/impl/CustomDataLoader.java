package com.izforge.izpack.installer.container.impl;

import java.util.List;

import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.event.InstallerListener;
import com.izforge.izpack.api.exception.InstallerException;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.data.CustomData;
import com.izforge.izpack.installer.data.UninstallData;
import com.izforge.izpack.installer.event.InstallerListeners;
import com.izforge.izpack.util.OsConstraintHelper;

/**
 * Reads the <em>customData</em> resource in order to populate the {@link InstallerListeners} and {@link UninstallData}.
 *
 * @author Anthonin Bonnefoy
 * @author Tim Anderson
 */
public class CustomDataLoader
{
    /**
     * The resources.
     */
    private final Resources resources;

    /**
     * The object factory.
     */
    private final ObjectFactory factory;

    /**
     * The installation data.
     */
    private AutomatedInstallData installData;

    /**
     * The uninstallation data.
     */
    private final UninstallData uninstallData;

    /**
     * The installer listeners.
     */
    private final InstallerListeners listeners;


    /**
     * Constructs a {@code CustomDataLoader}.
     *
     * @param resources     the resources
     * @param factory       the factory for listeners
     * @param installData   the installation data
     * @param uninstallData the uninstallation data
     * @param listeners     the installer listeners
     */
    public CustomDataLoader(Resources resources, ObjectFactory factory, AutomatedInstallData installData,
                            UninstallData uninstallData, InstallerListeners listeners)
    {
        this.resources = resources;
        this.factory = factory;
        this.installData = installData;
        this.uninstallData = uninstallData;
        this.listeners = listeners;
    }

    /**
     * Loads custom data.
     * <p/>
     * This includes:
     * <ul>
     * <li>installer listeners</li>
     * <li>uninstaller listeners</li>
     * <li>uninstaller jars</li>
     * <li>uninstaller native libraries</li>
     * </ul>
     * The {@link InstallerListener#afterInstallerInitialization} method will be invoked for each installer listener.
     *
     * @throws InstallerException if an {@link InstallerListener} throws an exception
     */
    @SuppressWarnings("unchecked")
    public void loadCustomData()
    {
        List<CustomData> customData = (List<CustomData>) resources.getObject("customData");
        for (CustomData data : customData)
        {
            if (data.osConstraints == null || OsConstraintHelper.oneMatchesCurrentSystem(data.osConstraints))
            {
                switch (data.type)
                {
                    case CustomData.INSTALLER_LISTENER:
                        addInstallerListener(data.listenerName);
                        break;
                    case CustomData.UNINSTALLER_LISTENER:
                        uninstallData.addUninstallerListener(data);
                        break;
                    case CustomData.UNINSTALLER_JAR:
                        uninstallData.addJar(data);
                        break;
                    case CustomData.UNINSTALLER_LIB:
                        uninstallData.addNativeLibrary(data.contents.get(0));
                        break;
                }
            }
        }
        notifyInstallerListeners();
    }

    /**
     * Invokes the {@link InstallerListener#afterInstallerInitialization} for each registered listener.
     *
     * @throws InstallerException if a listener throws an exception
     */
    private void notifyInstallerListeners()
    {
        try
        {
            listeners.afterInstallerInitialization(installData);
        }
        catch (InstallerException exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            throw new InstallerException(exception);
        }
    }

    /**
     * Adds an installer listener.
     *
     * @param className the listener class name
     */
    @SuppressWarnings("unchecked")
    private void addInstallerListener(String className)
    {
        InstallerListener listener = factory.create(className, InstallerListener.class);
        listeners.add(listener);
    }

}