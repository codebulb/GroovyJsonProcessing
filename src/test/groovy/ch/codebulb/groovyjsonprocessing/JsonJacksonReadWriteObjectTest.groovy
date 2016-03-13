package ch.codebulb.groovyjsonprocessing

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*
import ch.codebulb.groovyjsonprocessing.model.*
import groovy.json.*
import groovy.transform.CompileStatic
import java.text.*

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.annotation.JsonAutoDetect

@CompileStatic
class JsonJacksonReadWriteObjectTest {
    private static final Customer TEST_CUSTOMER = new Customer("Max")
        .address(new Address("First Street", "Los Angeles"))
        .purchases([
                new Purchase(100, Date.parse("yyyy-MM-dd", "2016-03-13"))
        ])
        .discounts([(DiscountType.FIDELITY): 5])
        
    private static final String TEST_CUSTOMER_JSON = $/{
  "name" : "Max",
  "address" : {
    "street" : "First Street",
    "city" : "Los Angeles",
    "country" : null
  },
  "purchases" : [ {
    "price" : 100,
    "date" : "2016-03-13 00:00:00"
  } ],
  "discounts" : {
    "FIDELITY" : 5
  }
}/$
    
    @Test
    public void testWriteObject() {
        ObjectMapper mapper = new ObjectMapper()
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        String customerJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(TEST_CUSTOMER)
        assert customerJson.normalize() == TEST_CUSTOMER_JSON
        
    }
    
    @Test
    public void testReadObject() {
        ObjectMapper mapper = new ObjectMapper()
        Customer customerJson = mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).readValue(TEST_CUSTOMER_JSON, Customer)
        
        assert customerJson instanceof Customer
        assert customerJson.name == "Max"
        
        assert customerJson.address instanceof Address
        assert customerJson.address.street == "First Street"
        assert customerJson.address.city == "City of Los Angeles"
        assert customerJson.address.@city == "Los Angeles"
        
        assert customerJson.purchases instanceof List
        assert customerJson.purchases.size() == 1
        
        assert customerJson.purchases[0] instanceof Purchase
        assert customerJson.purchases[0].price == 100
        assert customerJson.purchases[0].date == Date.parse("yyyy-MM-dd HH:mm:ss", "2016-03-13 00:00:00")
        
        assert customerJson.discounts instanceof Map
        assert customerJson.discounts.keySet().size() == 1
        assert customerJson.discounts.keySet()[0] instanceof DiscountType
        assert customerJson.discounts.keySet()[0] == DiscountType.FIDELITY
        assert customerJson.discounts[DiscountType.FIDELITY] == 5
    }
}
