package renewal.awesome_travel_backoffice.hotel.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import renewal.awesome_travel_backoffice.hotel.utiles.HotelType;

@StaticMetamodel(Hotel.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Hotel_ {

	public static final String AMENITIES = "amenities";
	public static final String WEBSITE = "website";
	public static final String IMAGES = "images";
	public static final String ADDRESS = "address";
	public static final String DESCRIPTION = "description";
	public static final String IS_ACTIVE = "isActive";
	public static final String NUMBER = "number";
	public static final String PRICE = "price";
	public static final String NAME = "name";
	public static final String ID = "id";
	public static final String HOTEL_TYPE = "hotelType";
	public static final String EMAIL = "email";

	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#amenities
	 **/
	public static volatile ListAttribute<Hotel, String> amenities;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#website
	 **/
	public static volatile SingularAttribute<Hotel, String> website;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#images
	 **/
	public static volatile ListAttribute<Hotel, HotelImage> images;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#address
	 **/
	public static volatile SingularAttribute<Hotel, String> address;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#description
	 **/
	public static volatile SingularAttribute<Hotel, String> description;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#isActive
	 **/
	public static volatile SingularAttribute<Hotel, Boolean> isActive;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#number
	 **/
	public static volatile SingularAttribute<Hotel, String> number;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#price
	 **/
	public static volatile SingularAttribute<Hotel, Integer> price;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#name
	 **/
	public static volatile SingularAttribute<Hotel, String> name;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#id
	 **/
	public static volatile SingularAttribute<Hotel, Long> id;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#hotelType
	 **/
	public static volatile SingularAttribute<Hotel, HotelType> hotelType;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel
	 **/
	public static volatile EntityType<Hotel> class_;
	
	/**
	 * @see renewal.awesome_travel_backoffice.hotel.entity.Hotel#email
	 **/
	public static volatile SingularAttribute<Hotel, String> email;

}

