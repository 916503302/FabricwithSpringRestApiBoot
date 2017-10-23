#机构接口
  - base_url = localhost:8080

###查询api

   1.机构信息
   
    /org/getorg?orgname={id}                       
    method: GET
    return:  {” ID “:”XXX”,” OrganizationName “:”xxx”,” OrganizationType”:”xxx”}

   2.机构下用户信息
   
    /org/getuser?userid={id}
    method: GET
    return {"username":"xxx","user_buyproduct":{}}
     
     
   3.机构下产品信息

    /org/getproduct?productid={id}
    method:GET
    return {"productname":"XXX","producttype":{}}
    
   4.机构的产品汇总信息
   
    /org/getallproduct
    method:GET
    return {"product1":"xxx","product2":"xxx" ....}
    
   5.机构的用户汇总信息
   
    /org/uesrinfo?num={num1}&num2={num2}
    method:GET
    return {"user1":{}, "user2":{}}
    
   6.某一端时间内的产品销售情况
   
    /org/productbytime
    method:POST
    request: {"start":"23423243", "end":"44563656", "product":{}} 空字段返回所有产品
    response:{"product2":{}, "product2":{}}
    
   
    
    