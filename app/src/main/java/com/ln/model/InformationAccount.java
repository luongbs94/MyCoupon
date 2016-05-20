package com.ln.model;

/**
 * Created by Nhahv on 5/13/2016.
 */
public class InformationAccount {

    private String id, displayName, email, grantedScopes,
            idToken, photoUrl, serverAuthCode;

    public InformationAccount() {
    }

    public InformationAccount(String id, String email, String idToken, String photoUrl, String displayName) {
        this.id = id;
        this.email = email;
        this.idToken = idToken;
        this.photoUrl = photoUrl;
        this.displayName = displayName;
    }

    public InformationAccount(String id, String displayName, String email,
                              String grantedScopes, String idToken, String photoUrl, String serverAuthCode) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.grantedScopes = grantedScopes;
        this.idToken = idToken;
        this.photoUrl = photoUrl;
        this.serverAuthCode = serverAuthCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGrantedScopes() {
        return grantedScopes;
    }

    public void setGrantedScopes(String grantedScopes) {
        this.grantedScopes = grantedScopes;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getServerAuthCode() {
        return serverAuthCode;
    }

    public void setServerAuthCode(String serverAuthCode) {
        this.serverAuthCode = serverAuthCode;
    }
}
