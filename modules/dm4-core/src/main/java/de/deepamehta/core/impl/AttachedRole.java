package de.deepamehta.core.impl;

import de.deepamehta.core.Association;
import de.deepamehta.core.Role;
import de.deepamehta.core.model.RoleModel;

import org.codehaus.jettison.json.JSONObject;



abstract class AttachedRole implements Role {

    // ---------------------------------------------------------------------------------------------- Instance Variables

    private RoleModel model;
    private Association assoc;

    protected final EmbeddedService dms;

    // ---------------------------------------------------------------------------------------------------- Constructors

    protected AttachedRole(RoleModel model, Association assoc, EmbeddedService dms) {
        this.model = model;
        this.assoc = assoc;
        this.dms = dms;
    }

    // -------------------------------------------------------------------------------------------------- Public Methods



    // === Role Implementation ===

    @Override
    public long getPlayerId() {
        return model.getPlayerId();
    }

    @Override
    public String getRoleTypeUri() {
        return model.getRoleTypeUri();
    }

    // ---

    @Override
    public void setRoleTypeUri(String roleTypeUri) {
        // update memory
        model.setRoleTypeUri(roleTypeUri);
        // update DB
        storeRoleTypeUri();     // abstract
    }

    // ---

    @Override
    public Association getAssociation() {
        return assoc;
    }

    // ---

    @Override
    public RoleModel getModel() {
        return model;
    }

    // ---

    @Override
    public JSONObject toJSON() {
        return getModel().toJSON();
    }

    // ----------------------------------------------------------------------------------------- Package Private Methods

    abstract void storeRoleTypeUri();
}
