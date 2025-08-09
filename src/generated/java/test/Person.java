package test;

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
import java.math.BigDecimal;
import java.util.Objects;
import test.Person;
import test.Person.PersonBuilder;
import test.Person.PersonBuilderImpl;
import test.Person.PersonImpl;
import test.meta.PersonMeta;

import static java.util.Optional.ofNullable;

/**
 * @version 0.0.0
 */
@RosettaDataType(value="Person", builder=Person.PersonBuilderImpl.class, version="0.0.0")
@RuneDataType(value="Person", model="test", builder=Person.PersonBuilderImpl.class, version="0.0.0")
public interface Person extends RosettaModelObject {

	PersonMeta metaData = new PersonMeta();

	/*********************** Getter Methods  ***********************/
	String getFirstName();
	String getLastName();
	BigDecimal getAge();
	Boolean getIsActive();

	/*********************** Build Methods  ***********************/
	Person build();
	
	Person.PersonBuilder toBuilder();
	
	static Person.PersonBuilder builder() {
		return new Person.PersonBuilderImpl();
	}

	/*********************** Utility Methods  ***********************/
	@Override
	default RosettaMetaData<? extends Person> metaData() {
		return metaData;
	}
	
	@Override
	@RuneAttribute("@type")
	default Class<? extends Person> getType() {
		return Person.class;
	}
	
	@Override
	default void process(RosettaPath path, Processor processor) {
		processor.processBasic(path.newSubPath("firstName"), String.class, getFirstName(), this);
		processor.processBasic(path.newSubPath("lastName"), String.class, getLastName(), this);
		processor.processBasic(path.newSubPath("age"), BigDecimal.class, getAge(), this);
		processor.processBasic(path.newSubPath("isActive"), Boolean.class, getIsActive(), this);
	}
	

	/*********************** Builder Interface  ***********************/
	interface PersonBuilder extends Person, RosettaModelObjectBuilder {
		Person.PersonBuilder setFirstName(String firstName);
		Person.PersonBuilder setLastName(String lastName);
		Person.PersonBuilder setAge(BigDecimal age);
		Person.PersonBuilder setIsActive(Boolean isActive);

		@Override
		default void process(RosettaPath path, BuilderProcessor processor) {
			processor.processBasic(path.newSubPath("firstName"), String.class, getFirstName(), this);
			processor.processBasic(path.newSubPath("lastName"), String.class, getLastName(), this);
			processor.processBasic(path.newSubPath("age"), BigDecimal.class, getAge(), this);
			processor.processBasic(path.newSubPath("isActive"), Boolean.class, getIsActive(), this);
		}
		

		Person.PersonBuilder prune();
	}

	/*********************** Immutable Implementation of Person  ***********************/
	class PersonImpl implements Person {
		private final String firstName;
		private final String lastName;
		private final BigDecimal age;
		private final Boolean isActive;
		
		protected PersonImpl(Person.PersonBuilder builder) {
			this.firstName = builder.getFirstName();
			this.lastName = builder.getLastName();
			this.age = builder.getAge();
			this.isActive = builder.getIsActive();
		}
		
		@Override
		@RosettaAttribute("firstName")
		@RuneAttribute("firstName")
		public String getFirstName() {
			return firstName;
		}
		
		@Override
		@RosettaAttribute("lastName")
		@RuneAttribute("lastName")
		public String getLastName() {
			return lastName;
		}
		
		@Override
		@RosettaAttribute("age")
		@RuneAttribute("age")
		public BigDecimal getAge() {
			return age;
		}
		
		@Override
		@RosettaAttribute("isActive")
		@RuneAttribute("isActive")
		public Boolean getIsActive() {
			return isActive;
		}
		
		@Override
		public Person build() {
			return this;
		}
		
		@Override
		public Person.PersonBuilder toBuilder() {
			Person.PersonBuilder builder = builder();
			setBuilderFields(builder);
			return builder;
		}
		
		protected void setBuilderFields(Person.PersonBuilder builder) {
			ofNullable(getFirstName()).ifPresent(builder::setFirstName);
			ofNullable(getLastName()).ifPresent(builder::setLastName);
			ofNullable(getAge()).ifPresent(builder::setAge);
			ofNullable(getIsActive()).ifPresent(builder::setIsActive);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			Person _that = getType().cast(o);
		
			if (!Objects.equals(firstName, _that.getFirstName())) return false;
			if (!Objects.equals(lastName, _that.getLastName())) return false;
			if (!Objects.equals(age, _that.getAge())) return false;
			if (!Objects.equals(isActive, _that.getIsActive())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (firstName != null ? firstName.hashCode() : 0);
			_result = 31 * _result + (lastName != null ? lastName.hashCode() : 0);
			_result = 31 * _result + (age != null ? age.hashCode() : 0);
			_result = 31 * _result + (isActive != null ? isActive.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "Person {" +
				"firstName=" + this.firstName + ", " +
				"lastName=" + this.lastName + ", " +
				"age=" + this.age + ", " +
				"isActive=" + this.isActive +
			'}';
		}
	}

	/*********************** Builder Implementation of Person  ***********************/
	class PersonBuilderImpl implements Person.PersonBuilder {
	
		protected String firstName;
		protected String lastName;
		protected BigDecimal age;
		protected Boolean isActive;
		
		@Override
		@RosettaAttribute("firstName")
		@RuneAttribute("firstName")
		public String getFirstName() {
			return firstName;
		}
		
		@Override
		@RosettaAttribute("lastName")
		@RuneAttribute("lastName")
		public String getLastName() {
			return lastName;
		}
		
		@Override
		@RosettaAttribute("age")
		@RuneAttribute("age")
		public BigDecimal getAge() {
			return age;
		}
		
		@Override
		@RosettaAttribute("isActive")
		@RuneAttribute("isActive")
		public Boolean getIsActive() {
			return isActive;
		}
		
		@Override
		@RosettaAttribute("firstName")
		@RuneAttribute("firstName")
		public Person.PersonBuilder setFirstName(String _firstName) {
			this.firstName = _firstName == null ? null : _firstName;
			return this;
		}
		
		@Override
		@RosettaAttribute("lastName")
		@RuneAttribute("lastName")
		public Person.PersonBuilder setLastName(String _lastName) {
			this.lastName = _lastName == null ? null : _lastName;
			return this;
		}
		
		@Override
		@RosettaAttribute("age")
		@RuneAttribute("age")
		public Person.PersonBuilder setAge(BigDecimal _age) {
			this.age = _age == null ? null : _age;
			return this;
		}
		
		@Override
		@RosettaAttribute("isActive")
		@RuneAttribute("isActive")
		public Person.PersonBuilder setIsActive(Boolean _isActive) {
			this.isActive = _isActive == null ? null : _isActive;
			return this;
		}
		
		@Override
		public Person build() {
			return new Person.PersonImpl(this);
		}
		
		@Override
		public Person.PersonBuilder toBuilder() {
			return this;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public Person.PersonBuilder prune() {
			return this;
		}
		
		@Override
		public boolean hasData() {
			if (getFirstName()!=null) return true;
			if (getLastName()!=null) return true;
			if (getAge()!=null) return true;
			if (getIsActive()!=null) return true;
			return false;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public Person.PersonBuilder merge(RosettaModelObjectBuilder other, BuilderMerger merger) {
			Person.PersonBuilder o = (Person.PersonBuilder) other;
			
			
			merger.mergeBasic(getFirstName(), o.getFirstName(), this::setFirstName);
			merger.mergeBasic(getLastName(), o.getLastName(), this::setLastName);
			merger.mergeBasic(getAge(), o.getAge(), this::setAge);
			merger.mergeBasic(getIsActive(), o.getIsActive(), this::setIsActive);
			return this;
		}
	
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			Person _that = getType().cast(o);
		
			if (!Objects.equals(firstName, _that.getFirstName())) return false;
			if (!Objects.equals(lastName, _that.getLastName())) return false;
			if (!Objects.equals(age, _that.getAge())) return false;
			if (!Objects.equals(isActive, _that.getIsActive())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (firstName != null ? firstName.hashCode() : 0);
			_result = 31 * _result + (lastName != null ? lastName.hashCode() : 0);
			_result = 31 * _result + (age != null ? age.hashCode() : 0);
			_result = 31 * _result + (isActive != null ? isActive.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "PersonBuilder {" +
				"firstName=" + this.firstName + ", " +
				"lastName=" + this.lastName + ", " +
				"age=" + this.age + ", " +
				"isActive=" + this.isActive +
			'}';
		}
	}
}
