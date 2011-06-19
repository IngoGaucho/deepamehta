package de.deepamehta.core;



/**
 * Definition of an association between 2 topic types -- part of DeepaMehta's type system,
 * like an association in a class diagram. Used to represent both, aggregations and compositions.
 *
 * @author <a href="mailto:jri@deepamehta.de">Jörg Richter</a>
 */
public interface AssociationDefinition extends Association {

    String getInstanceLevelAssocTypeUri();

    String getWholeTopicTypeUri();

    String getPartTopicTypeUri();

    String getWholeRoleTypeUri();

    String getPartRoleTypeUri();

    String getWholeCardinalityUri();

    String getPartCardinalityUri();

    ViewConfiguration getViewConfig();

    // ---

    void setWholeCardinalityUri(String wholeCardinalityUri);

    void setPartCardinalityUri(String partCardinalityUri);
}
