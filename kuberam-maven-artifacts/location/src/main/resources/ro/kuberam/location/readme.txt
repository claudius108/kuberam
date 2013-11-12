keo is an XQuery extension module for eXist, designated to provide various geo data.

For installing it, please follow the steps below:

1. Copy keo.jar into $EXIST_HOME/lib/extensions.
2. Copy GeoLiteCity.dat into $EXIST_HOME/lib/extensions/keo/GeoIP. This file can be downloaded 
from http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz.
3. Add
	<module class="ro.kuberam.keo.keoModule" uri="http://kuberam.ro/ns/keo" />
to builtin-modules in $EXIST_HOME/conf.xml.

Copyright notice:
"This product includes GeoLite data created by MaxMind, available from http://maxmind.com/".


Example of usage:

xquery version "1.0";
declare namespace keo="http://kuberam.ro/ns/keo";
let $ip-address :=
	if ( starts-with(request:get-remote-addr(), '192' ) )
	then ( '89.33.60.139' )
	else ( request:get-remote-addr() )
return
	<test-results>
		<test-description>Results for keo, an XQuery extension module for eXist dealing with geo data</test-description>
		<country-code>{ keo:get-country-code( $ip-address ) }</country-code>
		<country-name>{ keo:get-country-name( $ip-address ) }</country-name>
		<city>{ keo:get-city( $ip-address ) }</city>
		<time-zone>{ keo:get-time-zone( $ip-address ) }</time-zone>
		<distance>{ keo:get-distance( $ip-address, '89.33.60.139' ) }</distance>
	</test-results>