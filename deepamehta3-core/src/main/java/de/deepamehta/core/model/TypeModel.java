package de.deepamehta.core.model;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;



public abstract class TypeModel extends TopicModel {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private String dataTypeUri;
    private Set<IndexMode> indexModes;
    private Map<String, AssociationDefinitionModel> assocDefModels; // is never null, may be empty
    private ViewConfigurationModel viewConfigModel;                 // is never null

    private Logger logger = Logger.getLogger(getClass().getName());

    // ---------------------------------------------------------------------------------------------------- Constructors

    public TypeModel(String uri, TopicValue value, String topicTypeUri, String dataTypeUri) {
        super(uri, value, topicTypeUri);
        this.dataTypeUri = dataTypeUri;
        this.indexModes = new HashSet();
        this.assocDefModels = new LinkedHashMap();
        this.viewConfigModel = new ViewConfigurationModel();
    }

    public TypeModel(TopicModel model) {
        super(model);
    }

    public TypeModel(TypeModel model) {
        super(model);
        this.dataTypeUri = model.getDataTypeUri();
        this.indexModes = model.getIndexModes();
        this.assocDefModels = model.getAssocDefs();
        this.viewConfigModel = model.getViewConfigModel();
    }

    public TypeModel(JSONObject typeModel, String typeUri) {
        super(typeModel, typeUri);
        try {
            this.dataTypeUri = typeModel.getString("data_type_uri");
            this.indexModes = IndexMode.parse(typeModel);
            this.assocDefModels = new LinkedHashMap();
            this.viewConfigModel = new ViewConfigurationModel(typeModel);
            parseAssocDefs(typeModel);
        } catch (Exception e) {
            throw new RuntimeException("Parsing TypeModel failed (JSONObject=" + typeModel + ")", e);
        }
    }

    // -------------------------------------------------------------------------------------------------- Public Methods

    // === Data Type ===

    public String getDataTypeUri() {
        return dataTypeUri;
    }

    public void setDataTypeUri(String dataTypeUri) {
        this.dataTypeUri = dataTypeUri;
    }

    // === Index Modes ===

    public Set<IndexMode> getIndexModes() {
        return indexModes;
    }

    public void setIndexModes(Set<IndexMode> indexModes) {
        this.indexModes = indexModes;
    }

    // === Association Definitions ===

    public Map<String, AssociationDefinitionModel> getAssocDefs() {
        return assocDefModels;
    }

    public void setAssocDefs(Map<String, AssociationDefinitionModel> assocDefModels) {
        this.assocDefModels = assocDefModels;
    }

    public AssociationDefinitionModel getAssocDef(String assocDefUri) {
        AssociationDefinitionModel model = assocDefModels.get(assocDefUri);
        if (model == null) {
            throw new RuntimeException("Schema violation: association definition \"" +
                assocDefUri + "\" not found in " + this);
        }
        return model;
    }

    public void addAssocDef(AssociationDefinitionModel assocDef) {
        String assocDefUri = assocDef.getUri();
        // sanity check ### FIXME: drop this check or provide proper feedback to the type editor user
        if (!getDataTypeUri().equals("dm3.core.composite")) {
            throw new RuntimeException("Association definitions can only be added to composite topic types. " +
                "Topic type \"" + getUri() + "\" is of data type \"" + getDataTypeUri() + "\". (" + assocDef + ")");
        }
        // error check
        AssociationDefinitionModel existing = assocDefModels.get(assocDefUri);
        if (existing != null) {
            throw new RuntimeException("Schema ambiguity: topic type \"" + uri + "\" has more than one " +
                "association definitions with uri \"" + assocDefUri + "\" -- Use distinct role types at position 2");
        }
        //
        updateAssocDef(assocDef);
    }

    public void updateAssocDef(AssociationDefinitionModel assocDef) {
        assocDefModels.put(assocDef.getUri(), assocDef);
    }

    public AssociationDefinitionModel removeAssocDef(String assocDefUri) {
        // error check
        getAssocDef(assocDefUri);
        //
        return assocDefModels.remove(assocDefUri);
    }

    // === View Configuration ===

    public ViewConfigurationModel getViewConfigModel() {
        return viewConfigModel;
    }

    // FIXME: server-side operations on the view config settings possibly suggest they are not acually
    // view config settings but part of the topic type model. Possibly this method should be dropped.
    public Object getViewConfig(String typeUri, String settingUri) {
        return viewConfigModel.getSetting(typeUri, settingUri);
    }

    public void setViewConfig(ViewConfigurationModel viewConfigModel) {
        this.viewConfigModel = viewConfigModel;
    }



    // ****************************
    // *** TopicModel Overrides ***
    // ****************************



    @Override
    public JSONObject toJSON() {
        try {
            JSONObject o = super.toJSON();
            //
            o.put("data_type_uri", getDataTypeUri());
            IndexMode.toJSON(indexModes, o);
            AssociationDefinitionModel.toJSON(assocDefModels.values(), o);
            getViewConfigModel().toJSON(o);
            //
            return o;
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed (" + this + ")", e);
        }
    }



    // ****************
    // *** Java API ***
    // ****************



    @Override
    public String toString() {
        return "id=" + id + ", uri=\"" + uri + "\", value=" + value + ", typeUri=\"" + typeUri +
            "\", dataTypeUri=\"" + getDataTypeUri() + "\", indexModes=" + getIndexModes() + ", assocDefs=" +
            getAssocDefs() + ",\n    topic type " + getViewConfigModel();
    }



    // ------------------------------------------------------------------------------------------------- Private Methods

    private void parseAssocDefs(JSONObject typeModel) throws Exception {
        JSONArray models = typeModel.optJSONArray("assoc_defs");
        if (models != null) {
            for (int i = 0; i < models.length(); i++) {
                addAssocDef(new AssociationDefinitionModel(models.getJSONObject(i), this.uri));
            }
        }
    }
}
