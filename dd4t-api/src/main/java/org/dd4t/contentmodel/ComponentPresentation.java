package org.dd4t.contentmodel;

import org.dd4t.core.databind.BaseViewModel;

import java.util.Map;

public interface ComponentPresentation {

    /**
     * Get the component
     *
     * @return
     */
    public Component getComponent();

    /**
     * Set the component
     *
     * @param component
     */
    public void setComponent(Component component);

    /**
     * Get the component template
     *
     * @return
     */
    public ComponentTemplate getComponentTemplate();

    /**
     * Set the component template
     *
     * @param componentTemplate
     */
    public void setComponentTemplate(ComponentTemplate componentTemplate);

    /**
     * Get the rendered content
     */
    public String getRenderedContent();

    /**
     * Set the rendered content
     *
     * @param renderedContent
     */
    public void setRenderedContent(String renderedContent);

    /**
     * Return true if the component presentation is dynamic (i.e. available in the broker database as a separate item)
     *
     * @return
     */
    public boolean isDynamic();

    void setIsDynamic (boolean b);

    void setOrderOnPage (int i);

    void setViewModel(Map<String,BaseViewModel> models);

    Map<String,BaseViewModel> getAllViewModels();

    BaseViewModel getViewModel (String modelName);
}
