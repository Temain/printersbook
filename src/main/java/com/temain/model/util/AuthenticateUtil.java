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


//    public UserDTO getUser(String name, String password) {
//        Session session = null;
//        List<User> users = null;
//        try{
//            session = HibernateUtil.getSessionFactory().openSession();
//            Criteria c = session.createCriteria(User.class);
//            users = c.list();
//            if (users != null) {
//                for(User user : users){
//                    // Check hash by username
//                    if(name.equals(user.getName()) && BCrypt.checkpw(password, user.getHash()) /* && password.equals(user.getPassword())*/){
//                        session.beginTransaction();
//                        String sessionID = this.getThreadLocalRequest().getSession().getId();
//                        Date lastVisit = new Date();
//                        user.setSessionID(sessionID);
//                        user.setLastVisit(lastVisit);
//                        session.update(user);
//                        session.getTransaction().commit();
//                        return createUserDTO(user);
//                    }
//                }
//            }
//        }catch (Exception e) {
//            log.severe("Error on authenticate (UserServiceImpl): " + e.getMessage());
//        }finally {
//            if (session != null && session.isOpen()) {
//                session.close();
//            }
//        }
//        return null;
//    }
//
//    public void createPasswordHash(int userID) {
//        String hash = null;
//        Session session = null;
//        User user = null;
//        try{
//            session = HibernateUtil.getSessionFactory().openSession();
//            session.beginTransaction();
//            user = (User) session.get(User.class, userID);
//            hash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
//            user.setHash(hash);
//            session.update(user);
//            session.getTransaction().commit();
//        }catch (Exception e) {
//            log.severe("Error on hash password: " + e.getMessage());
//        }finally {
//            if (session != null && session.isOpen()) {
//                session.close();
//            }
//        }
//    }
	
	public String getUserName(){
		return currentUser.getName();
	}
}
