package edu.rpi.tw.escience.waterquality;

public interface UnionComponent extends GraphComponentCollection {
	int size();
	GraphComponentCollection getUnionComponent(int i);
}
