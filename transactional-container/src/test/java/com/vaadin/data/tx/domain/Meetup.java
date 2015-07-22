/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.tx.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean for tests
 *
 * @author Vaadin Ltd
 */
public class Meetup implements Serializable {
    private long id;
    private String description;
    private Date when;
    private Location location;

    public Meetup() {
    }

    public Meetup(long id) {
        this.id = id;
    }

    public Meetup(long id, String description, Date when, Location location) {
        this.id = id;
        this.description = description;
        this.when = when;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meetup meetup = (Meetup) o;

        if (id != meetup.id) return false;
        if (description != null ? !description.equals(meetup.description) : meetup.description != null) return false;
        if (when != null ? !when.equals(meetup.when) : meetup.when != null) return false;
        return !(location != null ? !location.equals(meetup.location) : meetup.location != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (when != null ? when.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

}
