package fr.miage.services;

import fr.miage.dao.UserDAO;
import fr.miage.model.Authentication;
import fr.miage.model.User;

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
