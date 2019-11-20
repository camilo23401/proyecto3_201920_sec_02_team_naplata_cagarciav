package model.logic;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.teamdev.jxmaps.Circle;
import com.teamdev.jxmaps.CircleOptions;
import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.InfoWindowOptions;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.Polyline;
import com.teamdev.jxmaps.PolylineOptions;
import com.teamdev.jxmaps.swing.MapView;

import model.data_structures.ArregloDinamico;
import model.data_structures.GrafoNoDirigido;

public class Maps extends MapView {

	// Objeto Google Maps
	private Map map;
	private MVCModelo modelo;

	//Coordenadas del camino a mostrar (secuencia de localizaciones (Lat, Long)) //ACA DEBEN IR LOS VERTICEs
	public LatLng[] locations = {new LatLng(4.6285797,-74.0649341), new LatLng(4.608550, -74.076443), new LatLng(4.601363, -74.0661), new LatLng(4.5954979,-74.068708) }; //Coordenadas de los vertices inicio, intermedio y fin.		
	public Maps(ArregloDinamico<Coordenadas> pArreglo, GrafoNoDirigido<Integer, Coordenadas> pSubGrafo)
	{	
		setOnMapReadyHandler( new MapReadyHandler() {
			@Override
			public void onMapReady(MapStatus status)
			{
				ArregloDinamico<Coordenadas> aux = pArreglo;
				LatLng[] rta = new LatLng[aux.darTamano()];
				for(int i=0; i< aux.darTamano();i++)
				{
					Coordenadas actual = aux.darElementoPos(i);
					rta[i] = new LatLng(actual.darLatitud(), actual.darLongitud());
				}
				locations = rta;
				if ( status == MapStatus.MAP_STATUS_OK )
				{
					map = getMap();

					// Configuracion de localizaciones intermedias del path (circulos)
					CircleOptions middleLocOpt= new CircleOptions(); 
					middleLocOpt.setFillColor("#00FF00");  // color de relleno
					middleLocOpt.setFillOpacity(0.5);
					middleLocOpt.setStrokeWeight(1.0);

					for(int i=0; i<locations.length;i++)
					{
						Circle middleLoc1 = new Circle(map);
						middleLoc1.setOptions(middleLocOpt);
						middleLoc1.setCenter(locations[i]); 
						middleLoc1.setRadius(20); //Radio del circulo
					}

					//Configuracion de la linea del camino
					PolylineOptions pathOpt = new PolylineOptions();
					pathOpt.setStrokeColor("#FFFF00");	  // color de linea	
					pathOpt.setStrokeOpacity(1.75);
					pathOpt.setStrokeWeight(1.5);
					pathOpt.setGeodesic(false);
					GrafoNoDirigido<Integer, Coordenadas>sub = pSubGrafo;
					for (int i = 0; i < sub.darCapacidad(); i++) {
						Coordenadas actual=sub.getVertexpos(i);
						if(actual!=null) {
							ArregloDinamico<Integer>adj=sub.adyacentes(sub.getVertexPosi(i));
							LatLng act=new LatLng(actual.darLatitud(), actual.darLongitud());
							for (int j = 0; j < adj.darTamano(); j++) {

								Coordenadas act2=sub.getInfoVertex(adj.darElementoPos(j));

								LatLng[] locations1 = {act,new LatLng(act2.darLatitud(), act2.darLongitud())};
								Polyline path = new Polyline(map); 														
								path.setOptions(pathOpt); 
								path.setPath(locations1);
							}
						}
					}
					System.out.println("Finaliza carga");
					initMap( map );
				}
			}
		}
				);
	}
	public void recuperarCoordenadasVertices(ArregloDinamico<Coordenadas> pArreglo)
	{
		ArregloDinamico<Coordenadas> aux = pArreglo;
		LatLng[] rta = new LatLng[aux.darTamano()];
		for(int i=0; i< aux.darTamano();i++)
		{
			Coordenadas actual = aux.darElementoPos(i);
			rta[i] = new LatLng(actual.darLatitud(), actual.darLongitud());
		}
		locations = rta;
		updateUI();
	}

	public void initMap(Map map)
	{
		MapOptions mapOptions = new MapOptions();
		MapTypeControlOptions controlOptions = new MapTypeControlOptions();
		controlOptions.setPosition(ControlPosition.BOTTOM_LEFT);
		mapOptions.setMapTypeControlOptions(controlOptions);

		map.setOptions(mapOptions);
		map.setCenter(locations[2]);
		map.setZoom(14.0);

	}

	public void initFrame(String titulo)
	{
		JFrame frame = new JFrame(titulo);
		frame.setSize(800, 800);
		frame.add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
}