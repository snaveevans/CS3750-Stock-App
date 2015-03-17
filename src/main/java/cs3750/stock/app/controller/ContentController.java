package cs3750.stock.app.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cs3750.stock.app.model.IncrementInvestment;
import cs3750.stock.app.model.Intializer;
import cs3750.stock.app.model.MainModel;
import cs3750.stock.app.model.MyYapi;
import cs3750.stock.app.model.Stock;
import cs3750.stock.app.model.Transaction;
import cs3750.stock.app.model.User;

@Controller
public class ContentController {
	@Autowired
	private NamedParameterJdbcTemplate jdbc;
	
    @RequestMapping("/login")
    public String getLogin(Model m){
        return "login";
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping("/successfulLogin")
    public String setupModel(Principal principal, Map<String, Object> model){
    	String mapping = "intialize";
    	
    	//-------------------------------------------------------------
    	//Put user in session
    	//-------------------------------------------------------------
    	String username = principal.getName(); //get logged in username
        
        String SQL = "SELECT * FROM users WHERE username = :username";  
		SqlParameterSource namedParameters = new MapSqlParameterSource("username", String.valueOf(username));
		User user = (User) jdbc.queryForObject(SQL, namedParameters, new UserMapper());

		MainModel.setUser(user);
		
		//-------------------------------------------------------------
    	//Determine next page
    	//-------------------------------------------------------------
		List<Transaction> transactions = (List<Transaction>) getTransactionByUserId(user.getUserId());
		
		if(transactions != null && transactions.size() > 0)
		{
			mapping = "viewstocks";
		}
		else
		{
			Intializer intializer = new Intializer();
			model.put("intializeForm", intializer);
		}
		
        return mapping;
    }
    
	@RequestMapping("/getAllStocks")
	public @ResponseBody Object getAllStocks(ModelMap model){
		Map<String, Object> data = new HashMap<>();
		data.put("STCK_ID", 1);
		data.put("STCK_SYMBL", 2);
		data.put("STCK_PRICE", 3);
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Stock> stocks = jdbc.query("select * from STOCKS", new BeanPropertyRowMapper(Stock.class)); 
		return stocks;
	}
	
	@RequestMapping("/getStock/{stockId}")
	public @ResponseBody Object getSomeStock(@PathVariable int stockId){
		Map<String, Object> data = new HashMap<>();
		data.put("STCK_ID", stockId);
		
		@SuppressWarnings("unchecked")
		List<Stock> stocks = jdbc.query("select * from STOCKS where stck_id = :STCK_ID", data, new BeanPropertyRowMapper(Stock.class) );		
		return stocks.isEmpty() ? null : stocks.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public void insertTransaction(Integer stockId, Integer userId, Integer stockQnty){
		String sql = "insert into transactions (STCK_ID, USER_ID, STCK_QNTY) values (:STCK_ID, :USER_ID, :STCK_QNTY)";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_ID", stockId);
		data.put("USER_ID", userId);
		data.put("STCK_QNTY", stockQnty);
		jdbc.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	public void insertTransaction(Integer stockId, Integer userId, Integer stockQnty, NamedParameterJdbcTemplate jdbcIn){
		String sql = "insert into transactions ( STCK_ID, USER_ID, STCK_QNTY) values ( :STCK_ID, :USER_ID, :STCK_QNTY)";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_ID", stockId);
		data.put("USER_ID", userId);
		data.put("STCK_QNTY", stockQnty);
		jdbcIn.update(sql, data);
	}
	
	
	@SuppressWarnings("unchecked")
	public void insertStocks(String symbol, double price){
		String sql = "insert into stocks (STCK_SYMBL, STCK_PRICE) values (:STCK_SYMBL, :STCK_PRICE)";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_SYMBL", symbol);
		data.put("STCK_PRICE", price);
		jdbc.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	public void insertStocks(String symbol, double price, NamedParameterJdbcTemplate jdbcIn){
		String sql = "insert into stocks (STCK_SYMBL, STCK_PRICE) values (:STCK_SYMBL, :STCK_PRICE)";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_SYMBL", symbol);
		data.put("STCK_PRICE", price);
		jdbcIn.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/insertUser")
	public @ResponseBody Object insertUser(String firstName, String lastName, double balance){
		String sql = "insert into users (USER_ID, FIRST_NAME, LAST_NAME, BALANCE) values (:USER_ID, :FIRST_NAME, :LAST_NAME, :BALANCE)";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("USER_ID", null);
		data.put("FIRST_NAME", firstName);
		data.put("LAST_NAME", lastName);
		data.put("BALANCE", balance);
		jdbc.update(sql, data);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void updateStockPrice(MyYapi stockUpdate){
		String sql = "update stocks set STCK_PRICE = :STCK_PRICE where STCK_SYMBL = :STCK_SYMBL";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_PRICE", stockUpdate.getPrice());
		data.put("STCK_SYMBL", stockUpdate.getSymbol());
		jdbc.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	public void updateStockPrice(String symbol, double price, NamedParameterJdbcTemplate jdbcIn){
		String sql = "update stocks set STCK_PRICE = :STCK_PRICE where STCK_SYMBL = :STCK_SYMBL";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_PRICE", price);
		data.put("STCK_SYMBL", symbol);
		jdbcIn.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	public void updateStockQnty(Integer transId, Integer stockQnty){
		String sql = "update transactions set STCK_QNTY = :STCK_QNTY where TRANS_ID = :TRANS_ID";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_QNTY", stockQnty);
		data.put("TRANS_ID", transId);
		jdbc.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	public void updateBalance(Integer userId, double balance){
		String sql = "update users set BALANCE = :BALANCE where USER_ID = :USER_ID";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("BALANCE", balance);
		data.put("USER_ID", userId);
		jdbc.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	public void updateBalance(Integer userId, double balance, NamedParameterJdbcTemplate jdbcIn){
		String sql = "update users set BALANCE = :BALANCE where USER_ID = :USER_ID";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("BALANCE", balance);
		data.put("USER_ID", userId);
		jdbcIn.update(sql, data);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getStocks")
	public @ResponseBody Object getStocks(Integer stockId){
		   String SQL = "SELECT * FROM stocks WHERE STCK_ID = :STCK_ID";  
		   SqlParameterSource namedParameters = new MapSqlParameterSource("STCK_ID", Integer.valueOf(stockId));  
		   Stock stocks = (Stock) jdbc.queryForObject(SQL, namedParameters, new StockMapper());  
		 return stocks; 
	}
	
	@SuppressWarnings("unchecked")
	public Stock getStockBySymbol(String symbol){
		   String SQL = "SELECT * FROM stocks WHERE STCK_SYMBL = :STCK_SYMBL";  
		   SqlParameterSource namedParameters = new MapSqlParameterSource("STCK_SYMBL", symbol);
		   Stock stock;
		   try{
		   stock = (Stock) jdbc.queryForObject(SQL, namedParameters, new StockMapper());
		   }
		   catch(Exception e){
			   stock = null;
		   }
		   
		 return stock; 
	}
	
	@SuppressWarnings("unchecked")
	public Stock getStockBySymbol(String symbol, NamedParameterJdbcTemplate jdbcIn){
		   String SQL = "SELECT * FROM stocks WHERE STCK_SYMBL = :STCK_SYMBL";  
		   SqlParameterSource namedParameters = new MapSqlParameterSource("STCK_SYMBL", symbol);
		   Stock stock;
		   try{
		   stock = (Stock) jdbcIn.queryForObject(SQL, namedParameters, new StockMapper());
		   }
		   catch(Exception e){
			   stock = null;
		   }
		   
		 return stock; 
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getUser")
	public @ResponseBody Object getUser(Integer userId){
		   String SQL = "SELECT * FROM users WHERE USER_ID = :USER_ID";  
		   SqlParameterSource namedParameters = new MapSqlParameterSource("USER_ID", Integer.valueOf(userId));  
		   User user = (User) jdbc.queryForObject(SQL, namedParameters, new UserMapper());  
		 return user; 
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getTransaction")
	public @ResponseBody Object getTransaction(Integer transId){
		   String SQL = "SELECT * FROM transactions WHERE TRANS_ID = :TRANS_ID";  
		   SqlParameterSource namedParameters = new MapSqlParameterSource("TRANS_ID", Integer.valueOf(transId));  
		   Transaction transaction = (Transaction) jdbc.queryForObject(SQL, namedParameters, new TransactionMapper()); 
		 return transaction; 
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/getTransactionByUser")
	public @ResponseBody Object getTransactionByUserId(Integer userId){
		   String SQL = "SELECT * FROM transactions WHERE USER_ID = :USER_ID";  
		   SqlParameterSource namedParameters = new MapSqlParameterSource("USER_ID", Integer.valueOf(userId));  
		   List<Transaction> transactions = (List<Transaction>) jdbc.query(SQL, namedParameters, new TransactionMapper()); 
		 return transactions; 
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/TestGrab")
	public @ResponseBody Object TestGrab(){
		MyYapi test = new MyYapi("TM");
		String sql = "insert into stocks (STCK_ID, STCK_SYMBL, STCK_PRICE) values (:STCK_ID, :STCK_SYMBL, :STCK_PRICE)";
		@SuppressWarnings("rawtypes")
		Map data = new HashMap();
		data.put("STCK_ID", null);
		data.put("STCK_SYMBL", test.getSymbol());
		data.put("STCK_PRICE", test.getPrice());
		jdbc.update(sql, data);
		return true;
	}
	
	public void invest(double balance, String[] symbol, NamedParameterJdbcTemplate jdbcIn) {
		User user = MainModel.getUser();//holds current user
		Integer userId = user.getUserId();	//holds current user id
		Integer[] stockId = {0,0,0};					//holds stock id
		MyYapi[] stock = {null, null, null}; 					//stock object to fetch price
		double[] price = {0,0,0};		//prices of each stock
		double[] invested = {0,0,0};	//amounts invested in each stock
		Integer[] qty = {0,0,0};			//number of stocks
		double div = 0;					//the user's balance split equally between the three stocks
		double mod = 0;					//what is left over after purchasing as much of one stock as possible 
		double remainder = 0;			//the total of the mod variables from each stock

		div = balance/3;	//split initial balance into 3
		
		//STOCK 1
		for (int i = 0; i < 3; i++)		//invest in the stocks
		{
			stock[i] = new MyYapi(symbol[i]);
			price[i] = stock[i].getPrice();
			
			mod = div % price[i];
			remainder += mod;
			
			invested[i] += div - mod;
			qty[i] += (int)(invested[i] / price[i]);
			
			System.out.println(symbol[i]);
			System.out.println(invested[i]);
			System.out.println(qty[i]);
		}
		
		boolean enoughLeft = true;
		while (enoughLeft) {
			enoughLeft = false;
			
			for (int i = 0; i < 3; i++) {
				if (remainder >= price[i]) //if there is enough left over to invest in a stock, invest and set flag to check again
				{
					remainder -= price[i];
					invested[i] += price[i];
					qty[i]++;
					enoughLeft = true;
				}
			}
			
		}
		
		for (int i = 0; i < 3; i++)	//loop through the three stocks
		{
			if (getStockBySymbol(symbol[i], jdbcIn) == null) {	//if there is no such stock, insert
				insertStocks(symbol[i], price[i], jdbcIn);
			} else {							//else update
				updateStockPrice(symbol[i], price[i], jdbcIn);
			}
			stockId[i] = getStockBySymbol(symbol[i], jdbcIn).getStck_id();	//get stock Id
			insertTransaction(stockId[i], userId, qty[i], jdbcIn);			//insert transaction for this stock
		}
		
		updateBalance(userId, remainder, jdbcIn);	//put the final remaining balance that could not be invested into the user table
		
		//REMAINDER
		System.out.println("REMAINDER:");
		System.out.println(remainder);
	}	
	
	@RequestMapping(value="/increaseInvestment", method = RequestMethod.GET)
	public String viewIncreaseInvestment(Map<String, Object> model) {
		IncrementInvestment increase = new IncrementInvestment();
		model.put("increaseInvestmentForm", increase);
		return "increaseInvestment";
		
	}
	
	
	@RequestMapping(value = "/increaseInvestment", method = RequestMethod.POST)
	public String processIncreaseInvestment(@ModelAttribute("increaseInvestmentForm") IncrementInvestment increase) {
		
		//System.out.println(intializer.getSymbol1() + " " + intializer.getSymbol2() + " " + intializer.getSymbol3());
		String array = increase.getSymbol();
		//MainModel.setStockArray(array);
		//MainModel.getUser().setBalance(intializer.getBalance());
		//new ContentController().invest(intializer.getBalance(), array, jdbc);
		
		return "viewstocks";
		
	}
	
	
	
}
