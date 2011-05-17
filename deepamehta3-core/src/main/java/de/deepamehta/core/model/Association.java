package de.deepamehta.core.model;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * Specification of an association -- A n-ary connection between topics and other associations.
 *
 * @author <a href="mailto:jri@deepamehta.de">Jörg Richter</a>
 */
public interface Association {

    long getId();

    String getTypeUri();

    // ---

    Role getRole1();

    Role getRole2();

    // ---

    Topic getTopic(String roleTypeUri);

    Set<Topic> getTopics(String roleTypeUri);

    // === Traversal ===

    Topic getRelatedTopic(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                                                     String othersTopicTypeUri,
                                                                     boolean fetchComposite);

    Set<RelatedTopic> getRelatedTopics(String assocTypeUri, String myRoleTypeUri, String othersRoleTypeUri,
                                                                                  String othersTopicTypeUri,
                                                                                  boolean fetchComposite);

    // ---

    JSONObject toJSON();
}
