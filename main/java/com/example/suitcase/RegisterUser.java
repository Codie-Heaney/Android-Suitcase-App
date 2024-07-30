package com.example.suitcase;

//class for holding user data
public class RegisterUser {
    private String fullname;
    private String username;
    private String password;
    private String email;

    public RegisterUser(String fullname, String username, String password, String email){
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getFullname(){return fullname;}
    public String getUsername(){return username;}
    public String getPassword(){return password;}
    public String getEmail(){return email;}
}
