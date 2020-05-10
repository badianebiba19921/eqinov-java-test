package com.eqinov.recrutement.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class DataPointId implements Serializable{

	private static final long serialVersionUID = 653247005544490025L;
		
	private Long site;
	
	private LocalDateTime time;

	public Long getSite() {
		return site;
	}

	public void setSite(Long site) {
		this.site = site;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(site, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataPointId other = (DataPointId) obj;
		return Objects.equals(site, other.site) && Objects.equals(time, other.time);
	}
		
}