package com.example;

import com.example.Address;
import com.example.Address.AddressBuilder;
import com.example.Address.AddressBuilderImpl;
import com.example.Address.AddressImpl;
import com.example.meta.AddressMeta;
import com.rosetta.model.lib.RosettaModelObject;
import com.rosetta.model.lib.RosettaModelObjectBuilder;
import com.rosetta.model.lib.annotations.RosettaAttribute;
import com.rosetta.model.lib.annotations.RosettaDataType;
import com.rosetta.model.lib.annotations.RuneAttribute;
import com.rosetta.model.lib.annotations.RuneDataType;
import com.rosetta.model.lib.meta.RosettaMetaData;
import com.rosetta.model.lib.path.RosettaPath;
import com.rosetta.model.lib.process.BuilderMerger;
import com.rosetta.model.lib.process.BuilderProcessor;
import com.rosetta.model.lib.process.Processor;
import java.util.Objects;

import static java.util.Optional.ofNullable;

/**
 * @version 0.0.0
 */
@RosettaDataType(value="Address", builder=Address.AddressBuilderImpl.class, version="0.0.0")
@RuneDataType(value="Address", model="com", builder=Address.AddressBuilderImpl.class, version="0.0.0")
public interface Address extends RosettaModelObject {

	AddressMeta metaData = new AddressMeta();

	/*********************** Getter Methods  ***********************/
	String getStreet();
	String getCity();
	String getZipCode();

	/*********************** Build Methods  ***********************/
	Address build();
	
	Address.AddressBuilder toBuilder();
	
	static Address.AddressBuilder builder() {
		return new Address.AddressBuilderImpl();
	}

	/*********************** Utility Methods  ***********************/
	@Override
	default RosettaMetaData<? extends Address> metaData() {
		return metaData;
	}
	
	@Override
	@RuneAttribute("@type")
	default Class<? extends Address> getType() {
		return Address.class;
	}
	
	@Override
	default void process(RosettaPath path, Processor processor) {
		processor.processBasic(path.newSubPath("street"), String.class, getStreet(), this);
		processor.processBasic(path.newSubPath("city"), String.class, getCity(), this);
		processor.processBasic(path.newSubPath("zipCode"), String.class, getZipCode(), this);
	}
	

	/*********************** Builder Interface  ***********************/
	interface AddressBuilder extends Address, RosettaModelObjectBuilder {
		Address.AddressBuilder setStreet(String street);
		Address.AddressBuilder setCity(String city);
		Address.AddressBuilder setZipCode(String zipCode);

		@Override
		default void process(RosettaPath path, BuilderProcessor processor) {
			processor.processBasic(path.newSubPath("street"), String.class, getStreet(), this);
			processor.processBasic(path.newSubPath("city"), String.class, getCity(), this);
			processor.processBasic(path.newSubPath("zipCode"), String.class, getZipCode(), this);
		}
		

		Address.AddressBuilder prune();
	}

	/*********************** Immutable Implementation of Address  ***********************/
	class AddressImpl implements Address {
		private final String street;
		private final String city;
		private final String zipCode;
		
		protected AddressImpl(Address.AddressBuilder builder) {
			this.street = builder.getStreet();
			this.city = builder.getCity();
			this.zipCode = builder.getZipCode();
		}
		
		@Override
		@RosettaAttribute("street")
		@RuneAttribute("street")
		public String getStreet() {
			return street;
		}
		
		@Override
		@RosettaAttribute("city")
		@RuneAttribute("city")
		public String getCity() {
			return city;
		}
		
		@Override
		@RosettaAttribute("zipCode")
		@RuneAttribute("zipCode")
		public String getZipCode() {
			return zipCode;
		}
		
		@Override
		public Address build() {
			return this;
		}
		
		@Override
		public Address.AddressBuilder toBuilder() {
			Address.AddressBuilder builder = builder();
			setBuilderFields(builder);
			return builder;
		}
		
		protected void setBuilderFields(Address.AddressBuilder builder) {
			ofNullable(getStreet()).ifPresent(builder::setStreet);
			ofNullable(getCity()).ifPresent(builder::setCity);
			ofNullable(getZipCode()).ifPresent(builder::setZipCode);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			Address _that = getType().cast(o);
		
			if (!Objects.equals(street, _that.getStreet())) return false;
			if (!Objects.equals(city, _that.getCity())) return false;
			if (!Objects.equals(zipCode, _that.getZipCode())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (street != null ? street.hashCode() : 0);
			_result = 31 * _result + (city != null ? city.hashCode() : 0);
			_result = 31 * _result + (zipCode != null ? zipCode.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "Address {" +
				"street=" + this.street + ", " +
				"city=" + this.city + ", " +
				"zipCode=" + this.zipCode +
			'}';
		}
	}

	/*********************** Builder Implementation of Address  ***********************/
	class AddressBuilderImpl implements Address.AddressBuilder {
	
		protected String street;
		protected String city;
		protected String zipCode;
		
		@Override
		@RosettaAttribute("street")
		@RuneAttribute("street")
		public String getStreet() {
			return street;
		}
		
		@Override
		@RosettaAttribute("city")
		@RuneAttribute("city")
		public String getCity() {
			return city;
		}
		
		@Override
		@RosettaAttribute("zipCode")
		@RuneAttribute("zipCode")
		public String getZipCode() {
			return zipCode;
		}
		
		@Override
		@RosettaAttribute("street")
		@RuneAttribute("street")
		public Address.AddressBuilder setStreet(String _street) {
			this.street = _street == null ? null : _street;
			return this;
		}
		
		@Override
		@RosettaAttribute("city")
		@RuneAttribute("city")
		public Address.AddressBuilder setCity(String _city) {
			this.city = _city == null ? null : _city;
			return this;
		}
		
		@Override
		@RosettaAttribute("zipCode")
		@RuneAttribute("zipCode")
		public Address.AddressBuilder setZipCode(String _zipCode) {
			this.zipCode = _zipCode == null ? null : _zipCode;
			return this;
		}
		
		@Override
		public Address build() {
			return new Address.AddressImpl(this);
		}
		
		@Override
		public Address.AddressBuilder toBuilder() {
			return this;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public Address.AddressBuilder prune() {
			return this;
		}
		
		@Override
		public boolean hasData() {
			if (getStreet()!=null) return true;
			if (getCity()!=null) return true;
			if (getZipCode()!=null) return true;
			return false;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public Address.AddressBuilder merge(RosettaModelObjectBuilder other, BuilderMerger merger) {
			Address.AddressBuilder o = (Address.AddressBuilder) other;
			
			
			merger.mergeBasic(getStreet(), o.getStreet(), this::setStreet);
			merger.mergeBasic(getCity(), o.getCity(), this::setCity);
			merger.mergeBasic(getZipCode(), o.getZipCode(), this::setZipCode);
			return this;
		}
	
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			Address _that = getType().cast(o);
		
			if (!Objects.equals(street, _that.getStreet())) return false;
			if (!Objects.equals(city, _that.getCity())) return false;
			if (!Objects.equals(zipCode, _that.getZipCode())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (street != null ? street.hashCode() : 0);
			_result = 31 * _result + (city != null ? city.hashCode() : 0);
			_result = 31 * _result + (zipCode != null ? zipCode.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "AddressBuilder {" +
				"street=" + this.street + ", " +
				"city=" + this.city + ", " +
				"zipCode=" + this.zipCode +
			'}';
		}
	}
}
