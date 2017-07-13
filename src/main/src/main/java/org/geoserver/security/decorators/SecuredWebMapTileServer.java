/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import org.geotools.data.ResourceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.data.ows.GetCapabilitiesRequest;
import org.geotools.data.ows.GetCapabilitiesResponse;
import org.geotools.data.ows.Layer;
import org.geotools.data.wms.request.GetFeatureInfoRequest;
import org.geotools.data.wms.request.GetLegendGraphicRequest;
import org.geotools.data.wms.response.GetFeatureInfoResponse;
import org.geotools.data.wms.response.GetLegendGraphicResponse;
import org.geotools.data.wmts.model.WMTSCapabilities;
import org.geotools.data.wmts.WebMapTileServer;
import org.geotools.data.wmts.request.GetTileRequest;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.ows.ServiceException;
import org.geotools.tile.Tile;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Applies security around the web map server
 * @author Andrea Aime - GeoSolutions
 *
 */
public class SecuredWebMapTileServer extends WebMapTileServer {

    WebMapTileServer delegate;

    public SecuredWebMapTileServer(WebMapTileServer delegate) throws IOException, ServiceException {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public GetFeatureInfoRequest createGetFeatureInfoRequest(GetTileRequest getTileRequest) {
        return null;
    }

    @Override
    public GetTileRequest createGetTileRequest() {
        return delegate.createGetTileRequest();
    }

    // -------------------------------------------------------------------------------------------
    //
    // Purely delegated methods
    //
    // -------------------------------------------------------------------------------------------


    public GetCapabilitiesResponse issueRequest(GetCapabilitiesRequest request) throws IOException,
            ServiceException {
        if(delegate!=null) {
            return delegate.issueRequest(request);
        }else {
            return null;
        }
    }

    @Override
    public GetFeatureInfoResponse issueRequest(GetFeatureInfoRequest request) {
        return delegate.issueRequest(request);
    }

    @Override
    public Set<Tile> issueRequest(GetTileRequest request) throws ServiceException {
        return delegate.issueRequest(request);
    }

    @Override
    public WMTSCapabilities getCapabilities() {
        return delegate.getCapabilities();
    }

    @Override
    public GeneralEnvelope getEnvelope(Layer layer, CoordinateReferenceSystem crs) {
        return delegate.getEnvelope(layer, crs);
    }

    @Override
    public ServiceInfo getInfo() {
        return delegate.getInfo();
    }

    @Override
    public ResourceInfo getInfo(Layer resource) {
        return delegate.getInfo(resource);
    }

    @Override
    public void setLoggingLevel(Level newLevel) {
        delegate.setLoggingLevel(newLevel);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public String toString() {
        return "SecuredWebMapTileServer " + delegate.toString();
    }

}
