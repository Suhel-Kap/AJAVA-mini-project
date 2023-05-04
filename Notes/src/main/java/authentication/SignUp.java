package authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.JsonString;

/**
 * Servlet implementation class SignUp
 */
@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set CORS headers for the response
	    response.setHeader("Access-Control-Allow-Origin", "*");
	    response.setHeader("Access-Control-Allow-Methods", "POST");
	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	    
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        System.out.println(line);
	    }
	    JSONParser parser = new JSONParser(reader);
	    try {
			ArrayList<Object> list = parser.list();
			System.out.print(list.get(1));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		System.out.println("Email " + email + ", password " + password + ", confirmPassword " + confirmPassword);
		
		if(!isEmailValid(email)) {
			System.out.println("Email incorrect");
			response.sendError(500, "Incorrect email");
			return;
		}
		
		if(!password.equals(confirmPassword)) {
			System.out.println("Passwords do not match");
			response.sendError(501, "Passwords do not match");
			return;
		}
		
		if(password.length() < 6) {
			System.out.println("Password length less than 6");
			response.sendError(502, "Password length less than 6");
			return;
		}
		
		try {
			if(exists(email)) {
				System.out.println("User already exists");
				response.sendError(503, "User already exists");
				return;
			}
			if(register(email, password)) {
				System.out.println("User registerd successfully");
				Cookie cookie = new Cookie("loggedIn", "true");
				response.addCookie(cookie);
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.print("{'status': 'registered successfully'}");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doOptions(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    res.setHeader("Access-Control-Allow-Origin", "*");
    res.setHeader("Access-Control-Allow-Methods", "POST");
    res.setHeader("Access-Control-Allow-Headers", "Content-Type");
    res.setHeader("Access-Control-Max-Age", "86400");
}

	
	protected boolean register(String email, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO user_info VALUES (?, ?)");
		stmt.setString(1, email);
		stmt.setString(2, password);
		System.out.println("User registerd successfully1");
		Integer res = stmt.executeUpdate();
		System.out.println("Executed? " + res);
		return res == 1;
	}
	
	public boolean exists(String email) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql", "root", "");
		PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_info WHERE email=(?)");
		stmt.setString(1, email);
		ResultSet rst = stmt.executeQuery();
		System.out.print("Executed");
		return rst.next();
	}
	
	public static boolean isEmailValid(String email)
    {
        String emailRegex = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
                              
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
