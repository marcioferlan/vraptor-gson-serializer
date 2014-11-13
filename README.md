## ABOUT

It's basically an annotation to tell VRaptor to serialize your method's return to JSON format.

Instead of writing something like:

	public void list(){
		result.use(Results.json()).withoutRoot().from(dao.findAll()).recursive().serialize();
	}

You just annotate the method with @Json and let it return your Java objects, like:

	@Json
	public List<Person> list(){
		return dao.findAll();
	}

## INSTALATION

    git clone git://github.com/luizsignorelli/vraptor-gson-serializer.git
    cd vraptor-gson-serializer
    mvn install

## CONFIGURATION

1. No configuration needed.

## CHANGES

### 0.6.0

* Plugin ported to VRaptor 4.x


### 0.5.0

We've added a JsonInterceptor and a @Json annotation, so now you can do this:

```java
    @Resource
    public class CustomerController {

        private final Customers customers;

        public CustomerController(Customers customers) {
            this.customers = customers;
        }

        @Json @Get("/customer/byName")
        public List<Customer> findByName(String name){
            return Arrays.asList(customers.findByName(name));
        }
    }
```

The annotation marks the method to be intercepted, and it will just use the result.use(json()) to serialize the return of the method.

You can exclude some fields too:

```java
    @Resource
    public class CustomerController {

        private final Customers customers;

        public CustomerController(Customers customers) {
            this.customers = customers;
        }

        @Json(exclude = {"address, age"}) @Get("/customer/byName")
        public List<Customer> findByName(String name){
            return Arrays.asList(customers.findByName(name));
        }
    }
```

### 0.0.2

We've implemented the exclude funcionality. It works just like the XstreamSerializer.