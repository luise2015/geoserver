/* Copyright (c) 2013 - 2017 Boundless - http://boundlessgeo.com All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
 package com.boundlessgeo.gsr.translate.geometry;

import com.boundlessgeo.gsr.model.geometry.SpatialReference;
import com.boundlessgeo.gsr.model.geometry.SpatialReferenceWKID;
import com.boundlessgeo.gsr.model.geometry.SpatialReferenceWKT;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONBuilder;

public class SpatialReferenceEncoder {
    public static void toJson(SpatialReference sr, JSONBuilder json) {
        if (sr instanceof SpatialReferenceWKID) {
            final SpatialReferenceWKID wkid = (SpatialReferenceWKID) sr;
            json.object()
              .key("wkid").value(wkid.getWkid())
            .endObject();
        } else if (sr instanceof SpatialReferenceWKT) {
            SpatialReferenceWKT wkt = (SpatialReferenceWKT) sr;
            json.object()
              .key("wkt").value(wkt.getWkt())
            .endObject();
        }
    }

    public static SpatialReference fromJson(JSONObject json) {
        if (json.containsKey("wkid")) {
            return new SpatialReferenceWKID(json.getInt("wkid"));
        } else if (json.containsKey("wkt")) {
            return new SpatialReferenceWKT(json.getString("wkt"));
        }
        if (json.containsKey("uri")) {
            // TODO: I'm not sure how to look these up - need to check out how GeoServer does this for WFS requests.
            throw new RuntimeException("Spatial reference specified as URI - decoding these is not yet implemented.");
        }

        throw new JSONException("Could not determine spatial reference from JSON: " + json);
    }

    public static CoordinateReferenceSystem coordinateReferenceSystemFromJSON(JSON json) {
        //TODO: Delegate to fromJson and consolidate SpatialReference <-> CRS conversion
        if (!(json instanceof JSONObject)) {
            throw new JSONException("Spatial Reference must be encoded as JSON Object: was " + json);
        }
        JSONObject obj = (JSONObject) json;

        if (obj.containsKey("wkid")) {
            int wkid = obj.getInt("wkid");

            try {
                return CRS.decode("EPSG:" + wkid);
            } catch (FactoryException e) {
                throw new JSONException("SRID " + wkid + " does not correspond to any known spatial reference");
            }
        }

        if (obj.containsKey("wkt")) {
            String wkt = obj.getString("wkt");
            try {
                return CRS.parseWKT(wkt);
            } catch (FactoryException e) {
                throw new JSONException("wkt value (" + wkt + ") is not valid well-known text", e);
            }
        }

        if (obj.containsKey("uri")) {
            // TODO: I'm not sure how to look these up - need to check out how GeoServer does this for WFS requests.
            throw new RuntimeException("Spatial reference specified as URI - decoding these is not yet implemented.");
        }

        throw new JSONException("Could not determine spatial reference from JSON: " + json);
    }

    private static CoordinateReferenceSystem interpret(String wkid) throws FactoryException {
        String withEPSGPrefix;
        try {
            Integer asInteger = Integer.valueOf(wkid);
            withEPSGPrefix = "EPSG:" + asInteger;
        } catch (NumberFormatException e) {
            withEPSGPrefix = "EPSG:" + wkid;
        }

        return CRS.decode(withEPSGPrefix);
    }
}
