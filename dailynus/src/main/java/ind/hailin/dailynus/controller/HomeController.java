package ind.hailin.dailynus.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ind.hailin.dailynus.utils.DesEncryption;
import ind.hailin.dailynus.utils.DesException;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	@RequestMapping(value = "/androidLogin", method = RequestMethod.POST)
	public void replyLogin(HttpServletRequest request, Writer writer) {
		try {
			String str = null;
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			while ((str = reader.readLine()) != null) {
				stringBuilder.append(str);
			}
			str = stringBuilder.toString();
			str = DesEncryption.decryption(str);
			
			String username = str.substring(0, str.indexOf(":"));
			String password = str.substring(str.indexOf(":") + 1);
			logger.info("username:" + username + "   password:" + password);
			
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			
			subject.login(token);
			
			if(subject.isAuthenticated())
				writer.write("LoginSuccess");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			try {
				writer.write("AuthenticationException");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				writer.write("OtherException");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
