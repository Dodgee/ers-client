package uk.ac.aston.jonesja1.ersclient.service.api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UpdateLocationAPI {

    @POST("update/")
    Call<Void> update(@Body LocationUpdateRequest locationUpdateRequest);

    class LocationUpdateRequest {

        private String id;

        private double longitude;

        private double latitude;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}
