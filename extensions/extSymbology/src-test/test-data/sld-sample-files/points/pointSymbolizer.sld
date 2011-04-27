<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" 
		xsi:schemaLocation="http://www.opengis.net/sld StyledLayerDescriptor.xsd" 
		xmlns="http://www.opengis.net/sld" 
		xmlns:ogc="http://www.opengis.net/ogc" 
		xmlns:xlink="http://www.w3.org/1999/xlink" 
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<NamedLayer>
		<Name>cons_punt.shp</Name>
		<UserStyle>
			<Title>A boring default style</Title>
			<Abstract>A sample style that just prints out a purple circle</Abstract>
			<FeatureTypeStyle>
				<Rule>
					<Name>Rule 1</Name>
					<Title>PurpleFill</Title>
					<Abstract>A purple fill with an 11 pixel size</Abstract>
					<PointSymbolizer>
						<Graphic>
			 				<ExternalGraphic>
           							<OnlineResource
            							xlink:href="http://maps.massgis.state.ma.us/images/question_mark.gif"/>
          			 				<Format>image/gif</Format>
          						</ExternalGraphic>
          						<Size>40</Size>
							<Rotation>22</Rotation>
						</Graphic>
					</PointSymbolizer>
				</Rule>
		    </FeatureTypeStyle>
		</UserStyle>
	</NamedLayer>
</StyledLayerDescriptor>

