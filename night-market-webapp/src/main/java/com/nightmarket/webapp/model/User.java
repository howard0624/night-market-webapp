
package com.nightmarket.webapp.model;

public class User {
  private long id;
  private String name;
  private String email;
  private String passwordHash; 
  private String phone;
  private String avatarUrl;
  private int isVerified;
  

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

  public int getIsVerified() { return isVerified; }
  public void setIsVerified(int isVerified) { this.isVerified = isVerified; }
}
