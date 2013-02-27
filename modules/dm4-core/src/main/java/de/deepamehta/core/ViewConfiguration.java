package de.deepamehta.core;

import de.deepamehta.core.model.TopicModel;
import de.deepamehta.core.model.ViewConfigurationModel;



/**
 * @author <a href="mailto:jri@deepamehta.de">Jörg Richter</a>
 */
public interface ViewConfiguration {

    Iterable<Topic> getConfigTopics();

    void addSetting(String configTypeUri, String settingUri, Object value);

    void updateConfigTopic(TopicModel configTopic);

    // ---

    ViewConfigurationModel getModel();
}
