package io.landal.test.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author alandini
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static String TO_STRING_FORMAT_PATTERN = "%s@%s[id = %s]";

	public final static String ID = "id";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = AbstractEntity.ID)
	private Long id;

	protected AbstractEntity() {
	}

	protected AbstractEntity(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format(TO_STRING_FORMAT_PATTERN, getClass().getSimpleName(), hashCode(), getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractEntity)) {
			return false;
		}
		AbstractEntity other = (AbstractEntity) obj;
		return getId() != null && Objects.equals(getId(), other.getId());
	}

	@Override
	public int hashCode() {
		return 31;
	}

	///////// Getters/Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
