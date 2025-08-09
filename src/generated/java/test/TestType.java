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
import java.util.Objects;
import test.TestType;
import test.TestType.TestTypeBuilder;
import test.TestType.TestTypeBuilderImpl;
import test.TestType.TestTypeImpl;
import test.meta.TestTypeMeta;

import static java.util.Optional.ofNullable;

/**
 * @version 0.0.0
 */
@RosettaDataType(value="TestType", builder=TestType.TestTypeBuilderImpl.class, version="0.0.0")
@RuneDataType(value="TestType", model="test", builder=TestType.TestTypeBuilderImpl.class, version="0.0.0")
public interface TestType extends RosettaModelObject {

	TestTypeMeta metaData = new TestTypeMeta();

	/*********************** Getter Methods  ***********************/
	String getStringProperty();
	Integer getIntProperty();
	Boolean getBooleanProperty();

	/*********************** Build Methods  ***********************/
	TestType build();
	
	TestType.TestTypeBuilder toBuilder();
	
	static TestType.TestTypeBuilder builder() {
		return new TestType.TestTypeBuilderImpl();
	}

	/*********************** Utility Methods  ***********************/
	@Override
	default RosettaMetaData<? extends TestType> metaData() {
		return metaData;
	}
	
	@Override
	@RuneAttribute("@type")
	default Class<? extends TestType> getType() {
		return TestType.class;
	}
	
	@Override
	default void process(RosettaPath path, Processor processor) {
		processor.processBasic(path.newSubPath("stringProperty"), String.class, getStringProperty(), this);
		processor.processBasic(path.newSubPath("intProperty"), Integer.class, getIntProperty(), this);
		processor.processBasic(path.newSubPath("booleanProperty"), Boolean.class, getBooleanProperty(), this);
	}
	

	/*********************** Builder Interface  ***********************/
	interface TestTypeBuilder extends TestType, RosettaModelObjectBuilder {
		TestType.TestTypeBuilder setStringProperty(String stringProperty);
		TestType.TestTypeBuilder setIntProperty(Integer intProperty);
		TestType.TestTypeBuilder setBooleanProperty(Boolean booleanProperty);

		@Override
		default void process(RosettaPath path, BuilderProcessor processor) {
			processor.processBasic(path.newSubPath("stringProperty"), String.class, getStringProperty(), this);
			processor.processBasic(path.newSubPath("intProperty"), Integer.class, getIntProperty(), this);
			processor.processBasic(path.newSubPath("booleanProperty"), Boolean.class, getBooleanProperty(), this);
		}
		

		TestType.TestTypeBuilder prune();
	}

	/*********************** Immutable Implementation of TestType  ***********************/
	class TestTypeImpl implements TestType {
		private final String stringProperty;
		private final Integer intProperty;
		private final Boolean booleanProperty;
		
		protected TestTypeImpl(TestType.TestTypeBuilder builder) {
			this.stringProperty = builder.getStringProperty();
			this.intProperty = builder.getIntProperty();
			this.booleanProperty = builder.getBooleanProperty();
		}
		
		@Override
		@RosettaAttribute("stringProperty")
		@RuneAttribute("stringProperty")
		public String getStringProperty() {
			return stringProperty;
		}
		
		@Override
		@RosettaAttribute("intProperty")
		@RuneAttribute("intProperty")
		public Integer getIntProperty() {
			return intProperty;
		}
		
		@Override
		@RosettaAttribute("booleanProperty")
		@RuneAttribute("booleanProperty")
		public Boolean getBooleanProperty() {
			return booleanProperty;
		}
		
		@Override
		public TestType build() {
			return this;
		}
		
		@Override
		public TestType.TestTypeBuilder toBuilder() {
			TestType.TestTypeBuilder builder = builder();
			setBuilderFields(builder);
			return builder;
		}
		
		protected void setBuilderFields(TestType.TestTypeBuilder builder) {
			ofNullable(getStringProperty()).ifPresent(builder::setStringProperty);
			ofNullable(getIntProperty()).ifPresent(builder::setIntProperty);
			ofNullable(getBooleanProperty()).ifPresent(builder::setBooleanProperty);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			TestType _that = getType().cast(o);
		
			if (!Objects.equals(stringProperty, _that.getStringProperty())) return false;
			if (!Objects.equals(intProperty, _that.getIntProperty())) return false;
			if (!Objects.equals(booleanProperty, _that.getBooleanProperty())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (stringProperty != null ? stringProperty.hashCode() : 0);
			_result = 31 * _result + (intProperty != null ? intProperty.hashCode() : 0);
			_result = 31 * _result + (booleanProperty != null ? booleanProperty.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "TestType {" +
				"stringProperty=" + this.stringProperty + ", " +
				"intProperty=" + this.intProperty + ", " +
				"booleanProperty=" + this.booleanProperty +
			'}';
		}
	}

	/*********************** Builder Implementation of TestType  ***********************/
	class TestTypeBuilderImpl implements TestType.TestTypeBuilder {
	
		protected String stringProperty;
		protected Integer intProperty;
		protected Boolean booleanProperty;
		
		@Override
		@RosettaAttribute("stringProperty")
		@RuneAttribute("stringProperty")
		public String getStringProperty() {
			return stringProperty;
		}
		
		@Override
		@RosettaAttribute("intProperty")
		@RuneAttribute("intProperty")
		public Integer getIntProperty() {
			return intProperty;
		}
		
		@Override
		@RosettaAttribute("booleanProperty")
		@RuneAttribute("booleanProperty")
		public Boolean getBooleanProperty() {
			return booleanProperty;
		}
		
		@Override
		@RosettaAttribute("stringProperty")
		@RuneAttribute("stringProperty")
		public TestType.TestTypeBuilder setStringProperty(String _stringProperty) {
			this.stringProperty = _stringProperty == null ? null : _stringProperty;
			return this;
		}
		
		@Override
		@RosettaAttribute("intProperty")
		@RuneAttribute("intProperty")
		public TestType.TestTypeBuilder setIntProperty(Integer _intProperty) {
			this.intProperty = _intProperty == null ? null : _intProperty;
			return this;
		}
		
		@Override
		@RosettaAttribute("booleanProperty")
		@RuneAttribute("booleanProperty")
		public TestType.TestTypeBuilder setBooleanProperty(Boolean _booleanProperty) {
			this.booleanProperty = _booleanProperty == null ? null : _booleanProperty;
			return this;
		}
		
		@Override
		public TestType build() {
			return new TestType.TestTypeImpl(this);
		}
		
		@Override
		public TestType.TestTypeBuilder toBuilder() {
			return this;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public TestType.TestTypeBuilder prune() {
			return this;
		}
		
		@Override
		public boolean hasData() {
			if (getStringProperty()!=null) return true;
			if (getIntProperty()!=null) return true;
			if (getBooleanProperty()!=null) return true;
			return false;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public TestType.TestTypeBuilder merge(RosettaModelObjectBuilder other, BuilderMerger merger) {
			TestType.TestTypeBuilder o = (TestType.TestTypeBuilder) other;
			
			
			merger.mergeBasic(getStringProperty(), o.getStringProperty(), this::setStringProperty);
			merger.mergeBasic(getIntProperty(), o.getIntProperty(), this::setIntProperty);
			merger.mergeBasic(getBooleanProperty(), o.getBooleanProperty(), this::setBooleanProperty);
			return this;
		}
	
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			TestType _that = getType().cast(o);
		
			if (!Objects.equals(stringProperty, _that.getStringProperty())) return false;
			if (!Objects.equals(intProperty, _that.getIntProperty())) return false;
			if (!Objects.equals(booleanProperty, _that.getBooleanProperty())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (stringProperty != null ? stringProperty.hashCode() : 0);
			_result = 31 * _result + (intProperty != null ? intProperty.hashCode() : 0);
			_result = 31 * _result + (booleanProperty != null ? booleanProperty.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "TestTypeBuilder {" +
				"stringProperty=" + this.stringProperty + ", " +
				"intProperty=" + this.intProperty + ", " +
				"booleanProperty=" + this.booleanProperty +
			'}';
		}
	}
}
