package renewal.awesome_travel_backoffice.product.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;
import renewal.awesome_travel_backoffice.hotel.entity.Hotel;
import renewal.awesome_travel_backoffice.hotel.utiles.RoomType;

@StaticMetamodel(PackageHotel.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PackageHotel_ {

	public static final String BOOKED_ROOMS = "bookedRooms";
	public static final String CHECK_IN = "checkIn";
	public static final String PRICE = "price";
	public static final String HOTEL = "hotel";
	public static final String RESERVED_ROOMS = "reservedRooms";
	public static final String ID = "id";
	public static final String CHECK_OUT = "checkOut";
	public static final String ROOM_TYPE = "roomType";

	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#bookedRooms
	 **/
	public static volatile SingularAttribute<PackageHotel, Integer> bookedRooms;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#checkIn
	 **/
	public static volatile SingularAttribute<PackageHotel, LocalDate> checkIn;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#price
	 **/
	public static volatile SingularAttribute<PackageHotel, Integer> price;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#hotel
	 **/
	public static volatile SingularAttribute<PackageHotel, Hotel> hotel;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#reservedRooms
	 **/
	public static volatile SingularAttribute<PackageHotel, Integer> reservedRooms;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#id
	 **/
	public static volatile SingularAttribute<PackageHotel, Long> id;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#checkOut
	 **/
	public static volatile SingularAttribute<PackageHotel, LocalDate> checkOut;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel
	 **/
	public static volatile EntityType<PackageHotel> class_;
	
	/**
	 * @see renewal.awesome_travel_backoffice.product.entity.PackageHotel#roomType
	 **/
	public static volatile SingularAttribute<PackageHotel, RoomType> roomType;

}

