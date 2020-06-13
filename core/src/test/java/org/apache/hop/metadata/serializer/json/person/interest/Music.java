package org.apache.hop.metadata.serializer.json.person.interest;

import org.apache.hop.metadata.api.HopMetadataProperty;

import java.util.Objects;

public class Music implements IInterest {

  @HopMetadataProperty
  private InterestType type;

  @HopMetadataProperty
  private String name;

  @HopMetadataProperty
  private String description;

  public Music() {
  }

  public Music( String name, String description ) {
    this.type = InterestType.Entertainment;
    this.name = name;
    this.description = description;
  }

  @Override public boolean equals( Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    Music music = (Music) o;
    return type == music.type &&
      Objects.equals( name, music.name ) &&
      Objects.equals( description, music.description );
  }

  @Override public int hashCode() {
    return Objects.hash( type, name, description );
  }

  /**
   * Gets name
   *
   * @return value of name
   */
  @Override public String getName() {
    return name;
  }

  /**
   * @param name The name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Gets description
   *
   * @return value of description
   */
  @Override public String getDescription() {
    return description;
  }

  /**
   * @param description The description to set
   */
  public void setDescription( String description ) {
    this.description = description;
  }

  /**
   * Gets type
   *
   * @return value of type
   */
  public InterestType getType() {
    return type;
  }

  /**
   * @param type The type to set
   */
  public void setType( InterestType type ) {
    this.type = type;
  }
}
