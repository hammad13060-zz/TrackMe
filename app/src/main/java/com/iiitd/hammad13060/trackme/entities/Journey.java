package com.iiitd.hammad13060.trackme.entities;

/**
 * Created by hammad on 8/3/16.
 */
public class Journey {
    int _id;
    String _journey_topic;
    String _from;

    double _src_lat;
    double _src_long;

    double _dst_lat;
    double _dst_long;

    double _current_lat;
    double _current_long;


    private String title;

    public Journey(String _journey_topic, String _from, double _src_lat,double _src_long, double _dst_lat, double _dst_long, double _current_lat, double _current_long) {
        this._journey_topic = _journey_topic;
        this._from = _from;
        this._src_lat = _src_lat;
        this._src_long = _src_long;
        this._dst_lat = _dst_lat;
        this._dst_long = _dst_long;
        this._current_lat = _current_lat;
        this._current_long = _current_long;
    }

    public Journey(String _journey_topic, String _from, double _current_lat, double _current_long) {
        this._journey_topic = _journey_topic;
        this._from = _from;
        this._current_lat = _current_lat;
        this._current_long = _current_long;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_journey_topic() {
        return _journey_topic;
    }

    public void set_journey_topic(String _journey_topic) {
        this._journey_topic = _journey_topic;
    }

    public String get_from() {
        return _from;
    }

    public void set_from(String _from) {
        this._from = _from;
    }

    public double get_src_lat() {
        return _src_lat;
    }

    public void set_src_lat(double _src_lat) {
        this._src_lat = _src_lat;
    }

    public double get_src_long() {
        return _src_long;
    }

    public void set_src_long(double _src_long) {
        this._src_long = _src_long;
    }

    public double get_dst_lat() {
        return _dst_lat;
    }

    public void set_dst_lat(double _dst_lat) {
        this._dst_lat = _dst_lat;
    }

    public double get_dst_long() {
        return _dst_long;
    }

    public void set_dst_long(double _dst_long) {
        this._dst_long = _dst_long;
    }

    public double get_current_lat() {
        return _current_lat;
    }

    public void set_current_lat(double _current_lat) {
        this._current_lat = _current_lat;
    }

    public double get_current_long() {
        return _current_long;
    }

    public void set_current_long(double _current_long) {
        this._current_long = _current_long;
    }

    public String getTitle() {return this.title;}

    public void setTitle(String title) {this.title = title;}


    @Override
    public String toString() {
        return "Journey{" +
                "_id=" + _id +
                ", _journey_topic='" + _journey_topic + '\'' +
                ", _from='" + _from + '\'' +
                ", _src_lat=" + _src_lat +
                ", _src_long=" + _src_long +
                ", _dst_lat=" + _dst_lat +
                ", _dst_long=" + _dst_long +
                ", _current_lat=" + _current_lat +
                ", _current_long=" + _current_long +
                '}';
    }
}
