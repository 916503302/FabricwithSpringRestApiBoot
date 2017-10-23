package com.heartgo.controller;

import java.util.concurrent.atomic.AtomicLong;


import com.heartgo.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.heartgo.fabric.ClientBean;
import com.heartgo.fabric.End2end;


import com.heartgo.model.Greeting;




@RestController
public class RestApiController {

	public ClientBean newClientBean;
	public End2end end = new End2end();

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(),
				String.format(template, name));

	}

	@RequestMapping(value = "/admin/setclient", method = RequestMethod.GET)
	public void SetupClient() {

		newClientBean = end.InitClient();


	}


	@RequestMapping(value = "/admin/comclient", method = RequestMethod.GET)
	public void ComposeClient() {

		newClientBean = end.composeClient();

	}


	@RequestMapping(value = "/admin/install", method = RequestMethod.GET)
	public String Install() {

		end.InstallChainCode(newClientBean);
		end.InstantById(newClientBean);
		return newClientBean.getRunchannel().getResponse();


	}


	@RequestMapping(value = "/admin/createorg", method = RequestMethod.GET)
	public ResponseEntity<?> CreateOrg() {
		String[] str=new String[]{"createOrganization","123401","pingan","3"};
		end.Transaction(newClientBean, str);

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/admin/getorg", method = RequestMethod.GET)
	public ResponseEntity<?> GetOrg() {
		String[] str=new String[]{"createOrganization","123","pingan","3"};
		end.Transaction(newClientBean, str);

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}


	@RequestMapping(value = "/admin/query", method = RequestMethod.GET)
	public void Query() {

		String[] str=new String[] {"query", "129"};
		System.out.println("str:"+str.toString());
		end.QueryTransation(newClientBean, str);
		System.out.println("transaction ok");
	}

	@RequestMapping(value = "/admin/tran", method = RequestMethod.GET)
	public void INVOKE() {
		String[] str=new String[]{"createOrganization","129","pingan","5"};

		end.Transactionby(newClientBean, str);
	}
	@RequestMapping(value = "/admin/yz", method = RequestMethod.GET)
	public String HelloYaozhen() {

//		String json = "[{'day1':'work','day2':26},{'day1':123,'day2':26}";
		return "{fadsdf:sfd}";

	}
//	@RequestMapping("getUser")
//	public User getUser(){
//		User user = new User();
//		user.setEmail("9999999@qq.com");
//		user.setPassword("111111");
//		user.setUsername("yaozhen");
//		return user;
//	}
}



