package com.buddymap.services;

import com.buddymap.dao.UserDAO;
import com.buddymap.model.Authentication;
import com.buddymap.model.User;


public class AuthenticationService {
	public static boolean checkUserPassword(Authentication authent){
		if(authent != null && authent.getMail() != null && authent.getPassword() != null){
			UserDAO userDAO = new UserDAO();
			User user = userDAO.findByMail(authent.getMail());
			if(user != null && user.getPwd() != null){
				String password = authent.getPassword()+authent.getMail();
				if(user.getPwd().equals(CryptographyService.hashSaltedSHA512(password))){
					return true;
				}
			}
		}
		return false;
	}
}
