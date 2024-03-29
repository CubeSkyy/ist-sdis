package com.forkexec.pts.domain;

import java.lang.Comparable;

public class Tuple implements java.io.Serializable, Comparable<Tuple> {

  private int tag;
  private int value;

  public Tuple(int t, int v)  {
    this.tag = t;
    this.value = v;
  }

  public Integer getTag(){
    return this.tag;
  }

  public int getValue(){
    return this.value;
  }

  public void setTag(int t){
    this.tag = t;
  }

  public void setValue(int v){
    this.value = v;
  }

  @Override
  public int compareTo(Tuple t) {
    return this.getTag().compareTo(t.getTag());
  }
}
