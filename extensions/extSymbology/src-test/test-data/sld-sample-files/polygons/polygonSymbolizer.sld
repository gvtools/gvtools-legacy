<?xml version="1.0" encoding="UTF-8"?>

<StyledLayerDescriptor version="1.0.0"
    xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd"
    xmlns="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    
  <NamedLayer>
    <Name>poly_landmarks.shp</Name>
    <UserStyle>
      <FeatureTypeStyle>
        <FeatureTypeName>Feature</FeatureTypeName>
        <Rule>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>2.0</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>20.0</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
<PolygonSymbolizer>
<Fill>
  <CssParameter name="fill" ><ogc:Literal>#FF0000</ogc:Literal></CssParameter>
  <CssParameter name="fill-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
</Fill>
<Stroke>
  <CssParameter name="stroke" ><ogc:Literal>#808080</ogc:Literal></CssParameter>
  <CssParameter name="stroke-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-width" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linecap" ><ogc:Literal>butt</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linejoin" ><ogc:Literal>bevel</ogc:Literal></CssParameter>
  <CssParameter name="stroke-dashoffset" ><ogc:Literal>0.0</ogc:Literal></CssParameter>
</Stroke>
</PolygonSymbolizer>
        </Rule>
        <Rule>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>25.0</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>41.0</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
<PolygonSymbolizer>
<Fill>
  <CssParameter name="fill" ><ogc:Literal>#CC0033</ogc:Literal></CssParameter>
  <CssParameter name="fill-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
</Fill>
<Stroke>
  <CssParameter name="stroke" ><ogc:Literal>#808080</ogc:Literal></CssParameter>
  <CssParameter name="stroke-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-width" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linecap" ><ogc:Literal>butt</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linejoin" ><ogc:Literal>bevel</ogc:Literal></CssParameter>
  <CssParameter name="stroke-dashoffset" ><ogc:Literal>0.0</ogc:Literal></CssParameter>
</Stroke>
</PolygonSymbolizer>
        </Rule>
        <Rule>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>43.0</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>62.0</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
<PolygonSymbolizer>
<Fill>
  <CssParameter name="fill" ><ogc:Literal>#990066</ogc:Literal></CssParameter>
  <CssParameter name="fill-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
</Fill>
<Stroke>
  <CssParameter name="stroke" ><ogc:Literal>#808080</ogc:Literal></CssParameter>
  <CssParameter name="stroke-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-width" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linecap" ><ogc:Literal>butt</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linejoin" ><ogc:Literal>bevel</ogc:Literal></CssParameter>
  <CssParameter name="stroke-dashoffset" ><ogc:Literal>0.0</ogc:Literal></CssParameter>
</Stroke>
</PolygonSymbolizer>
        </Rule>
        <Rule>
          <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>65.0</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>85.0</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
<PolygonSymbolizer>
<Fill>
  <CssParameter name="fill" ><ogc:Literal>#660099</ogc:Literal></CssParameter>
  <CssParameter name="fill-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
</Fill>
<Stroke>
  <CssParameter name="stroke" ><ogc:Literal>#808080</ogc:Literal></CssParameter>
  <CssParameter name="stroke-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-width" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linecap" ><ogc:Literal>butt</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linejoin" ><ogc:Literal>bevel</ogc:Literal></CssParameter>
  <CssParameter name="stroke-dashoffset" ><ogc:Literal>0.0</ogc:Literal></CssParameter>
</Stroke>
</PolygonSymbolizer>
        </Rule>
        <Rule>
           <ogc:Filter>
            <ogc:And>
              <ogc:PropertyIsGreaterThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>87.0</ogc:Literal>
              </ogc:PropertyIsGreaterThanOrEqualTo>
              <ogc:PropertyIsLessThanOrEqualTo>
                <ogc:PropertyName>LAND</ogc:PropertyName>
                <ogc:Literal>110.0</ogc:Literal>
              </ogc:PropertyIsLessThanOrEqualTo>
            </ogc:And>
          </ogc:Filter>
<PolygonSymbolizer>
<Fill>
  <CssParameter name="fill" ><ogc:Literal>#3300CC</ogc:Literal></CssParameter>
  <CssParameter name="fill-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
</Fill>
<Stroke>
  <CssParameter name="stroke" ><ogc:Literal>#808080</ogc:Literal></CssParameter>
  <CssParameter name="stroke-opacity" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-width" ><ogc:Literal>1.0</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linecap" ><ogc:Literal>butt</ogc:Literal></CssParameter>
  <CssParameter name="stroke-linejoin" ><ogc:Literal>bevel</ogc:Literal></CssParameter>
  <CssParameter name="stroke-dashoffset" ><ogc:Literal>0.0</ogc:Literal></CssParameter>
</Stroke>
</PolygonSymbolizer>
        </Rule>
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>