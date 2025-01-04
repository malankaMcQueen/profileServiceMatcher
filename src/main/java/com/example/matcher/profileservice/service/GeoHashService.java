package com.example.matcher.profileservice.service;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import com.example.matcher.profileservice.model.GeoPoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
@AllArgsConstructor
public class GeoHashService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final double EARTH_RADIUS_KM = 6371.0; // Средний радиус Земли в километрах

    public GeoPoint decodeGeoHash(String geohash) {
        GeoHash geoHash = GeoHash.fromGeohashString(geohash);
        WGS84Point point = geoHash.getOriginatingPoint();
        return new GeoPoint(point.getLatitude(), point.getLongitude());
    }

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Перевод координат из градусов в радианы
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        // Разница широт и долгот
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        // Формула Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // Расстояние
        return EARTH_RADIUS_KM * c;
    }

    public String getCityByCoordinates(double latitude, double longitude) {
        String url = String.format(
                Locale.US,
                "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json&addressdetails=1",
                latitude, longitude
        );

        String response = restTemplate.getForObject(url, String.class);
        return parseCityFromResponse(response);
    }

    private String parseCityFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode address = jsonNode.get("address");
            if (address.has("city")) {
                return address.get("city").asText();
            } else if (address.has("town")) {
                return address.get("town").asText();
            } else if (address.has("village")) {
                return address.get("village").asText();
            } else {
                return "City not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }

//    @EventListener(ApplicationReadyEvent.class)
//    public void print() {
//        String geohashFirst = "u9ed31h0";
//        String geohashSecond = "u9ed994d";
//        double[] coordinatesFirst = decodeGeoHash(geohashFirst);
//        double[] coordinatesSecond = decodeGeoHash(geohashSecond);
//        double distance = calculateDistance(
//                coordinatesFirst[0], coordinatesFirst[1],
//                coordinatesSecond[0], coordinatesSecond[1]
//        );
//        System.out.println("Latitude: " + coordinatesFirst[0] + ", Longitude: " + coordinatesFirst[1]);
//        System.out.println("Latitude: " + coordinatesSecond[0] + ", Longitude: " + coordinatesSecond[1]);
//        System.out.println("Distance between points: " + distance + " km");
//        System.out.println("City: " + getCityByCoordinates(coordinatesFirst[0], coordinatesFirst[1]));
//        System.out.println("City: " + getCityByCoordinates(coordinatesSecond[0], coordinatesSecond[1]));
//    }
}
