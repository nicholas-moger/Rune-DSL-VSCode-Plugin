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
import test.SimpleType;
import test.SimpleType.SimpleTypeBuilder;
import test.SimpleType.SimpleTypeBuilderImpl;
import test.SimpleType.SimpleTypeImpl;
import test.meta.SimpleTypeMeta;

import static java.util.Optional.ofNullable;

/**
 * @version 0.0.0
 */
@RosettaDataType(value="SimpleType", builder=SimpleType.SimpleTypeBuilderImpl.class, version="0.0.0")
@RuneDataType(value="SimpleType", model="test", builder=SimpleType.SimpleTypeBuilderImpl.class, version="0.0.0")
public interface SimpleType extends RosettaModelObject {

	SimpleTypeMeta metaData = new SimpleTypeMeta();

	/*********************** Getter Methods  ***********************/
	String getName();
	Integer getValue();

	/*********************** Build Methods  ***********************/
	SimpleType build();
	
	SimpleType.SimpleTypeBuilder toBuilder();
	
	static SimpleType.SimpleTypeBuilder builder() {
		return new SimpleType.SimpleTypeBuilderImpl();
	}

	/*********************** Utility Methods  ***********************/
	@Override
	default RosettaMetaData<? extends SimpleType> metaData() {
		return metaData;
	}
	
	@Override
	@RuneAttribute("@type")
	default Class<? extends SimpleType> getType() {
		return SimpleType.class;
	}
	
	@Override
	default void process(RosettaPath path, Processor processor) {
		processor.processBasic(path.newSubPath("name"), String.class, getName(), this);
		processor.processBasic(path.newSubPath("value"), Integer.class, getValue(), this);
	}
	

	/*********************** Builder Interface  ***********************/
	interface SimpleTypeBuilder extends SimpleType, RosettaModelObjectBuilder {
		SimpleType.SimpleTypeBuilder setName(String name);
		SimpleType.SimpleTypeBuilder setValue(Integer value);

		@Override
		default void process(RosettaPath path, BuilderProcessor processor) {
			processor.processBasic(path.newSubPath("name"), String.class, getName(), this);
			processor.processBasic(path.newSubPath("value"), Integer.class, getValue(), this);
		}
		

		SimpleType.SimpleTypeBuilder prune();
	}

	/*********************** Immutable Implementation of SimpleType  ***********************/
	class SimpleTypeImpl implements SimpleType {
		private final String name;
		private final Integer value;
		
		protected SimpleTypeImpl(SimpleType.SimpleTypeBuilder builder) {
			this.name = builder.getName();
			this.value = builder.getValue();
		}
		
		@Override
		@RosettaAttribute("name")
		@RuneAttribute("name")
		public String getName() {
			return name;
		}
		
		@Override
		@RosettaAttribute("value")
		@RuneAttribute("value")
		public Integer getValue() {
			return value;
		}
		
		@Override
		public SimpleType build() {
			return this;
		}
		
		@Override
		public SimpleType.SimpleTypeBuilder toBuilder() {
			SimpleType.SimpleTypeBuilder builder = builder();
			setBuilderFields(builder);
			return builder;
		}
		
		protected void setBuilderFields(SimpleType.SimpleTypeBuilder builder) {
			ofNullable(getName()).ifPresent(builder::setName);
			ofNullable(getValue()).ifPresent(builder::setValue);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			SimpleType _that = getType().cast(o);
		
			if (!Objects.equals(name, _that.getName())) return false;
			if (!Objects.equals(value, _that.getValue())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (name != null ? name.hashCode() : 0);
			_result = 31 * _result + (value != null ? value.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "SimpleType {" +
				"name=" + this.name + ", " +
				"value=" + this.value +
			'}';
		}
	}

	/*********************** Builder Implementation of SimpleType  ***********************/
	class SimpleTypeBuilderImpl implements SimpleType.SimpleTypeBuilder {
	
		protected String name;
		protected Integer value;
		
		@Override
		@RosettaAttribute("name")
		@RuneAttribute("name")
		public String getName() {
			return name;
		}
		
		@Override
		@RosettaAttribute("value")
		@RuneAttribute("value")
		public Integer getValue() {
			return value;
		}
		
		@Override
		@RosettaAttribute("name")
		@RuneAttribute("name")
		public SimpleType.SimpleTypeBuilder setName(String _name) {
			this.name = _name == null ? null : _name;
			return this;
		}
		
		@Override
		@RosettaAttribute("value")
		@RuneAttribute("value")
		public SimpleType.SimpleTypeBuilder setValue(Integer _value) {
			this.value = _value == null ? null : _value;
			return this;
		}
		
		@Override
		public SimpleType build() {
			return new SimpleType.SimpleTypeImpl(this);
		}
		
		@Override
		public SimpleType.SimpleTypeBuilder toBuilder() {
			return this;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public SimpleType.SimpleTypeBuilder prune() {
			return this;
		}
		
		@Override
		public boolean hasData() {
			if (getName()!=null) return true;
			if (getValue()!=null) return true;
			return false;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public SimpleType.SimpleTypeBuilder merge(RosettaModelObjectBuilder other, BuilderMerger merger) {
			SimpleType.SimpleTypeBuilder o = (SimpleType.SimpleTypeBuilder) other;
			
			
			merger.mergeBasic(getName(), o.getName(), this::setName);
			merger.mergeBasic(getValue(), o.getValue(), this::setValue);
			return this;
		}
	
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || !(o instanceof RosettaModelObject) || !getType().equals(((RosettaModelObject)o).getType())) return false;
		
			SimpleType _that = getType().cast(o);
		
			if (!Objects.equals(name, _that.getName())) return false;
			if (!Objects.equals(value, _that.getValue())) return false;
			return true;
		}
		
		@Override
		public int hashCode() {
			int _result = 0;
			_result = 31 * _result + (name != null ? name.hashCode() : 0);
			_result = 31 * _result + (value != null ? value.hashCode() : 0);
			return _result;
		}
		
		@Override
		public String toString() {
			return "SimpleTypeBuilder {" +
				"name=" + this.name + ", " +
				"value=" + this.value +
			'}';
		}
	}
}
