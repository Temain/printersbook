package com.temain.model.util;

import java.util.Iterator;
import java.util.List;

import com.temain.model.Factory;
import com.temain.model.entities.User;

public class AuthenticateUtil {
	
	private static User currentUser = null;
	
	public boolean authenticate(String name, String password){
		Factory.getInstance();
		List<User> users = Factory.getUserDAO().getAllUsers();
		Iterator<User> it = users.iterator();
		while(it.hasNext()){
			User user = (User)it.next();
			if(name.equals(user.getName()) && password.equals(user.getPassword())){
				currentUser = user;
				return true;
			}
		}
		return false;
	}
	
	public boolean isUserInRole(String role){
		if(role.equals(currentUser.getRole().getName())){
			return true;
		}
		return false;
	}
	
	public String getUserName(){
		return currentUser.getName();
	}
}
