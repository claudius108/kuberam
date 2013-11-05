package ro.kuberam.location.ipLocalisation;

public class GetAltitude {
	
	
//	private double getElevationFromGoogleMaps(double longitude, double latitude) {
//        double result = Double.NaN;
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        String url = "http://maps.googleapis.com/maps/api/elevation/"
//                + "xml?locations=" + String.valueOf(latitude)
//                + "," + String.valueOf(longitude)
//                + "&sensor=true";
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = httpClient.execute(httpGet, localContext);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream instream = entity.getContent();
//                int r = -1;
//                StringBuffer respStr = new StringBuffer();
//                while ((r = instream.read()) != -1)
//                    respStr.append((char) r);
//                String tagOpen = "<elevation>";
//                String tagClose = "</elevation>";
//                if (respStr.indexOf(tagOpen) != -1) {
//                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
//                    int end = respStr.indexOf(tagClose);
//                    String value = respStr.substring(start, end);
//                    result = (double)(Double.parseDouble(value)*3.2808399); // convert from meters to feet
//                }
//                instream.close();
//            }
//        } catch (ClientProtocolException e) {} 
//        catch (IOException e) {}
//
//        return result;
//    }

}
