package com.izforge.izpack.test;

import com.izforge.izpack.api.data.GUIInstallData;
import com.izforge.izpack.api.substitutor.VariableSubstitutor;
import com.izforge.izpack.core.container.AbstractContainer;
import com.izforge.izpack.core.container.ConditionContainer;
import com.izforge.izpack.merge.resolve.ClassPathCrawler;
import com.izforge.izpack.merge.resolve.MergeableResolver;
import com.izforge.izpack.util.substitutor.VariableSubstitutorImpl;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.parameters.ComponentParameter;

import java.util.HashMap;
import java.util.Properties;

/**
 * Container for condition tests
 *
 * @author Anthonin Bonnefoy
 */
public class TestConditionContainer extends AbstractContainer
{
    public void fillContainer(MutablePicoContainer picoContainer)
    {
        pico
                .addComponent(GUIInstallData.class)
                .addComponent(VariableSubstitutor.class, VariableSubstitutorImpl.class)
                .addComponent(ClassPathCrawler.class)
                .addComponent(MutablePicoContainer.class, pico)
                .addComponent(MergeableResolver.class, MergeableResolver.class, new ComponentParameter(HashMap.class))
                .addComponent(HashMap.class)
                .addComponent(Properties.class)
                .addComponent(ConditionContainer.class)
                .addComponent(AbstractContainer.class, this)
                ;
    }
}
