/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamerker.io.e.valua_java.mainClasses;
/**
 *
 * @author hp
 */
public class Admin extends User {
    public Admin(String username, String displayName) {
        super(username, displayName);
    }

    @Override
    public String getRole() { return "admin"; }
}