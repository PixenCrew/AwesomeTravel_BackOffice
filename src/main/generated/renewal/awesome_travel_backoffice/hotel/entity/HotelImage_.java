package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(HotelImage.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class HotelImage_ {

	public static final String HOTEL = "hotel";
	public static final String ID = "id";
	public static final String URL = "url";

	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.HotelImage#hotel
	 **/
	public static volatile SingularAttribute<HotelImage, Hotel> hotel;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.HotelImage#id
	 **/
	public static volatile SingularAttribute<HotelImage, Long> id;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.HotelImage
	 **/
	public static volatile EntityType<HotelImage> class_;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.HotelImage#url
	 **/
	public static volatile SingularAttribute<HotelImage, String> url;

}

