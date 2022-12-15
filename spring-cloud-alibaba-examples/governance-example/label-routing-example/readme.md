# label route example

## Project Description

This project demonstrates how to use the spring cloud ailbaba governance labelrouting module to complete the label routing function.

## module structure

This module includes a consumer instance and a provider cluster, which contains two instances.

## Example

### How to access

**Note that this section is only for your convenience in understanding the access method. The access work has been completed in this sample code, and you do not need to modify it.**
1. First, modify the pom XML file, which introduces the spring cloud ailbaba governance labelrouting dependency.

      <dependency>
          <groupId>com.alibaba.cloud</groupId>
          <artifactId>spring-cloud-starter-alibaba-governance-labelrouting</artifactId>
      </dependency>

### Application Start

Start a startup class of three modules, ConsumerApplication and two ProviderApplications, and inject them into the Nacos registry.

### Effect demonstration

#### Rule Description
The rules set in the instance are as follows:

		@GetMapping("/add")
		public void getDataFromControlPlaneTest() {
List<RouteRule> routeRules = new ArrayList<>();
List<MatchService> matchServices = new ArrayList<>();

			UntiedRouteDataStructure untiedRouteDataStructure = new UntiedRouteDataStructure();
			untiedRouteDataStructure.setTargetService("service-provider");

			LabelRouteRule labelRouteData = new LabelRouteRule();
			labelRouteData.setDefaultRouteVersion("v1");

			RouteRule routeRule = new HeaderRule();
			routeRule.setType("header");
			routeRule.setCondition("=");
			routeRule.setKey("tag");
			routeRule.setValue("gray");
			RouteRule routeRule1 = new UrlRule.Parameter();
			routeRule1.setType("parameter");
			routeRule1.setCondition(">");
			routeRule1.setKey("id");
			routeRule1.setValue("10");
			RouteRule routeRule2 = new UrlRule.Path();
			routeRule2.setType("path");
			routeRule2.setCondition("=");
			routeRule2.setValue("/router-test");
			routeRules.add(routeRule);
			routeRules.add(routeRule1);
			routeRules.add(routeRule2);

			MatchService matchService = new MatchService();
			matchService.setVersion("v2");
			matchService.setWeight(100);
			matchService.setRuleList(routeRules);
			matchServices.add(matchService);

			labelRouteData.setMatchRouteList(matchServices);

			untiedRouteDataStructure.setLabelRouteRule(labelRouteData);

			List<UntiedRouteDataStructure> untiedRouteDataStructureList = new ArrayList<>();
			untiedRouteDataStructureList.add(untiedRouteDataStructure);
			controlPlaneConnection.pushRouteData(untiedRouteDataStructureList);
		}
The rules corresponding to the code are as follows:
If the request parameter contains tag=gray and the request header contains id and the value is greater than 10, uri is /router-test at the same time, the traffic is routed to the v2 version. If one of the request parameters does not meet the requirement, the traffic is routed to the v1 version.

Rules also support dynamic modification. The rules for testing dynamic modification are as follows:

		@GetMapping("/add")
		public void getDataFromControlPlaneTest() {
			List<RouteRule> routeRules = new ArrayList<>();
			List<MatchService> matchServices = new ArrayList<>();

			UntiedRouteDataStructure untiedRouteDataStructure = new UntiedRouteDataStructure();
			untiedRouteDataStructure.setTargetService("service-provider");

			LabelRouteRule labelRouteData = new LabelRouteRule();
			labelRouteData.setDefaultRouteVersion("v1");

			RouteRule routeRule = new HeaderRule();
			routeRule.setType("header");
			routeRule.setCondition("=");
			routeRule.setKey("tag");
			routeRule.setValue("gray");
			RouteRule routeRule1 = new UrlRule.Parameter();
			routeRule1.setType("parameter");
			routeRule1.setCondition(">");
			routeRule1.setKey("id");
			routeRule1.setValue("10");
			RouteRule routeRule2 = new UrlRule.Path();
			routeRule2.setType("path");
			routeRule2.setCondition("=");
			routeRule2.setValue("/router-test");
			routeRules.add(routeRule);
			routeRules.add(routeRule1);
			routeRules.add(routeRule2);

			MatchService matchService = new MatchService();
			matchService.setVersion("v2");
			matchService.setWeight(50);
			matchService.setRuleList(routeRules);
			matchServices.add(matchService);

			labelRouteData.setMatchRouteList(matchServices);

			untiedRouteDataStructure.setLabelRouteRule(labelRouteData);

			List<UntiedRouteDataStructure> untiedRouteDataStructureList = new ArrayList<>();
			untiedRouteDataStructureList.add(untiedRouteDataStructure);
			controlPlaneConnection.pushRouteData(untiedRouteDataStructureList);
		}
The rules corresponding to the code are as follows:
If the request parameter contains tag=gray, and the request header contains id and the value is greater than 10,uri is /router-test, 50% of the traffic is routed to the v2 version, and the rest is routed to the v1 version. If one of the traffic does not meet the requirements, the traffic is routed to the v1 version.

#####  demonstrate Steps
1. visit http://localhost:18083/add Push the routing rules from the control surface interface to the routing rule warehouse
   visit http://localhost:18083/router -The test does not meet the routing rules. When the test is routed to the v1 version, the v1 version instance prints and returns the following results:
   NacosRegistration{nacosDiscoveryProperties=NacosDiscoveryProperties{serverAddr='127.0.0.1:8848', endpoint='', namespace='', watchDelay=30000, logName='', service='service-provider', weight=1.0, clusterName='DEFAULT', group='DEFAULT_GROUP', namingLoadCacheAtStart='false', metadata={preserved.register.source=SPRING_CLOUD, version=v1}, registerEnabled=true, ip='XXX', networkInterface='', port=18082, secure=false, accessKey='', secretKey='', heartBeatInterval=null, heartBeatTimeout=null, ipDeleteTimeout=null, failFast=true}}
   visit http://localhost:18083/router-test?id=11 and the test value set in the request header is gray, which meets the routing rules. The route is to the v2 version. The v2 version instance prints and returns the following results:
   NacosRegistration{nacosDiscoveryProperties=NacosDiscoveryProperties{serverAddr='127.0.0.1:8848', endpoint='', namespace='', watchDelay=30000, logName='', service='service-provider', weight=1.0, clusterName='DEFAULT', group='DEFAULT_GROUP', namingLoadCacheAtStart='false', metadata={preserved.register.source=SPRING_CLOUD, version=v2}, registerEnabled=true, ip='XXX', networkInterface='', port=18081, secure=false, accessKey='', secretKey='', heartBeatInterval=null, heartBeatTimeout=null, ipDeleteTimeout=null, failFast=true}}

2. visit http://localhost:18083/update Simulate dynamic modification of routing rules.
   visit http://localhost:18083/router  The test does not meet the routing rules. When the test is routed to the v1 version, the v1 version instance prints and returns the following results:
   NacosRegistration{nacosDiscoveryProperties=NacosDiscoveryProperties{serverAddr='127.0.0.1:8848', endpoint='', namespace='', watchDelay=30000, logName='', service='service-provider', weight=1.0, clusterName='DEFAULT', group='DEFAULT_GROUP', namingLoadCacheAtStart='false', metadata={preserved.register.source=SPRING_CLOUD, version=v1}, registerEnabled=true, ip='XXX', networkInterface='', port=18082, secure=false, accessKey='', secretKey='', heartBeatInterval=null, heartBeatTimeout=null, ipDeleteTimeout=null, failFast=true}}
   visit http://localhost:18083/router-test?id=11 and the test value set in the request header is gray, which meets the routing rules. 50% of the routes are routed to the v2 version. The v2 version instance prints the following results:
   NacosRegistration{nacosDiscoveryProperties=NacosDiscoveryProperties{serverAddr='127.0.0.1:8848', endpoint='', namespace='', watchDelay=30000, logName='', service='service-provider', weight=1.0, clusterName='DEFAULT', group='DEFAULT_GROUP', namingLoadCacheAtStart='false', metadata={preserved.register.source=SPRING_CLOUD, version=v2}, registerEnabled=true, ip='XXX', networkInterface='', port=18081, secure=false, accessKey='', secretKey='', heartBeatInterval=null, heartBeatTimeout=null, ipDeleteTimeout=null, failFast=true}}
   50% of them are routed to the v1 version, and the following results are returned when the v1 version instance is printed:
   NacosRegistration{nacosDiscoveryProperties=NacosDiscoveryProperties{serverAddr='127.0.0.1:8848', endpoint='', namespace='', watchDelay=30000, logName='', service='service-provider', weight=1.0, clusterName='DEFAULT', group='DEFAULT_GROUP', namingLoadCacheAtStart='false', metadata={preserved.register.source=SPRING_CLOUD, version=v1}, registerEnabled=true, ip='XXX', networkInterface='', port=18082, secure=false, accessKey='', secretKey='', heartBeatInterval=null, heartBeatTimeout=null, ipDeleteTimeout=null, failFast=true}}

3. If you don't push rule,it will load balance by common rule you set.
## Integrating Istio
**Note that this section is only for your convenience in understanding the access method. The access work has been completed in this sample code, and you do not need to modify it.**
1. First, modify the pom.xml file to introduce the spring cloud ailbaba governance label-routing and istio-resource-transform dependency
```
   <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>spring-cloud-starter-alibaba-governance-labelrouting</artifactId>
   </dependency>
   <dependency>
      <groupId>com.alibaba.cloud</groupId>
      <artifactId>istio-resource-transform</artifactId>
   </dependency>
```
2. Configure application.yml for Istio control plane
```
server:
  port: 18084
spring:
  main:
    allow-bean-definition-overriding: true
  application:
    name: service-consumer
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        fail-fast: true
        username: nacos
        password: nacos
    governance:
      auth:
        # Is authentication enabled
        enabled: ${ISTIO_AUTH_ENABLE:false}
    istio:
      config:
        # Is Istio resource transform enabled
        enabled: ${ISTIO_CONFIG_ENABLE:true}
        # Istiod ip
        host: ${ISTIOD_ADDR:127.0.0.1}
        # Istiod port
        port: ${ISTIOD_PORT:15010}
        # Istiod thread-pool size
        polling-pool-size: ${POLLING_POOL_SIZE:10}
        # Istiod polling gap
        polling-time: ${POLLING_TIME:10}
        # Istiod token(For Istio 15012 port)
        istiod-token: ${ISTIOD_TOKEN:}
```
### Startup Application
Start IstioConsumerApplication and two ProviderApplications, and inject it into the Nacos registry center.

### Publish Configuration
We publish the label routing rules through the Istio control plane. We publish a DestinationRule rule first.
```
kubectl apply -f - << EOF
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: my-destination-rule
spec:
  host: sca-virtual-service
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
EOF
```
This rule splits the back-end service into two versions. Pod with label v1 is assigned to v1, and pod with label v2 is assigned to v2
After that, we publish the VirtualService rule
```
kubectl apply -f - << EOF
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: sca-virtual-service
spec:
  hosts:
    - service-provider
  http:
  - match:
    - headers:
        tag:
          exact: gray
      uri:
        exact: /istio-label-routing

    route:
    - destination:
        host: service-provider
        subset: v2
  - route:
    - destination:
        host: service-provider
        subset: v1
EOF
```
This VirtualService specifies the simplest label routing rule. HTTP requests with a gray header and /istio-label-routing path are routed to v2, and the rest of the traffic is routed to v1
### Demonstrate effect
We send an HTTP request without a request header to IstioConsumerApplication
```
curl --location --request GET '127.0.0.1:18084/istio-label-routing'
```
Since the request header is not gray, the request will be routed to version v1 with the following result
```
Route in 30.221.132.228: 18081,version is v1.
```
We then send an HTTP request with a gray tag in its header and the request path is /istio-label-routing
```
curl --location --request GET '127.0.0.1:18084/istio-label-routing' --header 'tag: gray'
```
The request is routed to version v2 because the routing rule is matched by the request.
```
Route in 30.221.132.228: 18081,version is v2.
```