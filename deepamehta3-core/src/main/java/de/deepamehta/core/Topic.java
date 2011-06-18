package de.deepamehta.core;

import de.deepamehta.core.model.TopicValue;

import java.util.List;
import java.util.Set;



/**
 * Specification of a topic -- DeepaMehta's central data object.
 *
 * @author <a href="mailto:jri@deepamehta.de">Jörg Richter</a>
 */
public interface Topic extends DeepaMehtaObject {



    // === Traversal ===

    // ### TODO: move this methods to DeepaMehtaObject.
    // ### Topic would be soley a marker interface then.

    TopicValue getChildTopicValue(String assocDefUri);

    void setChildTopicValue(String assocDefUri, TopicValue value);

    Set<RelatedTopic> getRelatedTopics(String assocTypeUri);

    /**
     * @param   assocTypeUri        may be null
     * @param   myRoleTypeUri       may be null
     * @param   othersRoleTypeUri   may be null
     * @param   othersTopicTypeUri  may be null
     */
    RelatedTopic getRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                                                            String othersTopicTypeUri,
                                                                            boolean fetchComposite);

    /**
     * @param   assocTypeUri        may be null
     * @param   myRoleTypeUri       may be null
     * @param   othersRoleTypeUri   may be null
     * @param   othersTopicTypeUri  may be null
     */
    Set<RelatedTopic> getRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                                                                  String othersTopicTypeUri,
                                                                                  boolean fetchComposite);

    /**
     * @param   assocTypeUris       may be null
     * @param   myRoleTypeUri       may be null
     * @param   othersRoleTypeUri   may be null
     * @param   othersTopicTypeUri  may be null
     */
    Set<RelatedTopic> getRelatedTopics(List assocTypeUris, String myRoleTypeUri, String othersRoleTypeUri,
                                                                                 String othersTopicTypeUri,
                                                                                 boolean fetchComposite);

    Set<Association> getAssociations(String myRoleTypeUri);
}
