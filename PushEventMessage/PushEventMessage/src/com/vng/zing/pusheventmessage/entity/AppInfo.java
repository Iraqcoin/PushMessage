/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.entity;

/**
 *
 * @author root
 */
public class AppInfo {
    private int id;
    private String name;
    private String logo;
    private int Error;
    public int getId() {
        return id;
    }

    public int getError() {
        return Error;
    }

    public void setError(int Error) {
        this.Error = Error;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    
}
