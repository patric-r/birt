
/*******************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/
package org.eclipse.birt.data.engine.olap.impl.query;

import java.util.Collection;

import org.eclipse.birt.data.engine.api.CollectionConditionalExpression;
import org.eclipse.birt.data.engine.api.IBaseExpression;
import org.eclipse.birt.data.engine.api.IFilterDefinition;
import org.eclipse.birt.data.engine.api.IScriptExpression;
import org.eclipse.birt.data.engine.api.querydefn.FilterDefinition;
import org.eclipse.birt.data.engine.api.querydefn.ScriptExpression;
import org.eclipse.birt.data.engine.olap.api.query.CubeFilterDefinition;
import org.eclipse.birt.data.engine.olap.api.query.CubeSortDefinition;
import org.eclipse.birt.data.engine.olap.api.query.ICubeElementFactory;
import org.eclipse.birt.data.engine.olap.api.query.ICubeFilterDefinition;
import org.eclipse.birt.data.engine.olap.api.query.ICubeOperationFactory;
import org.eclipse.birt.data.engine.olap.api.query.ICubeQueryDefinition;
import org.eclipse.birt.data.engine.olap.api.query.ICubeSortDefinition;
import org.eclipse.birt.data.engine.olap.api.query.ILevelDefinition;
import org.eclipse.birt.data.engine.olap.api.query.ISubCubeQueryDefinition;

/**
 * CubeElementFactory can be used to create the elements that are needed in
 * defining queries.
 */

public class CubeElementFactory implements ICubeElementFactory {
	/**
	 * Create a new ICubeQueryDefinition instance.
	 * 
	 * @return
	 */
	public ICubeQueryDefinition createCubeQuery(String name) {
		return new CubeQueryDefinition(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.data.engine.olap.api.query.ICubeElementFactory#
	 * createSubCubeQuery(java.lang.String)
	 */
	public ISubCubeQueryDefinition createSubCubeQuery(String name) {
		return new SubCubeQueryDefinition(name);
	}

	/**
	 * create a new ICubeFilterDefinition instance.
	 * 
	 * @return
	 */
	public ICubeFilterDefinition creatCubeFilterDefinition(IBaseExpression filterExpr, ILevelDefinition targetLevel,
			ILevelDefinition[] axisQulifierLevel, Object[] axisQulifierValue) {
		return new CubeFilterDefinition(filterExpr, targetLevel, axisQulifierLevel, axisQulifierValue);
	}

	/**
	 * create a new ICubeFilterDefinition instance.
	 * 
	 * @return
	 */
	public ICubeFilterDefinition creatCubeFilterDefinition(IBaseExpression filterExpr, ILevelDefinition targetLevel,
			ILevelDefinition[] axisQulifierLevel, Object[] axisQulifierValue, boolean updateAggr) {
		CubeFilterDefinition cubeFilterDefinition = new CubeFilterDefinition(filterExpr, targetLevel, axisQulifierLevel,
				axisQulifierValue);
		cubeFilterDefinition.setUpdateAggregation(updateAggr);
		return cubeFilterDefinition;
	}

	/**
	 * create a new ICubeFilterDefinition instance.
	 * 
	 * @return
	 */
	public IFilterDefinition creatLevelMemberFilterDefinition(Collection<IScriptExpression> targetLevels, int operator,
			Collection<Collection<IScriptExpression>> memberValues) {
		return new FilterDefinition(new CollectionConditionalExpression(targetLevels, operator, memberValues));
	}

	/**
	 * create a new ICubeSortDefinition instance.
	 */
	public ICubeSortDefinition createCubeSortDefinition(String filterExpr, ILevelDefinition targetLevel,
			ILevelDefinition[] axisQulifierLevel, Object[] axisQulifierValue, int sortDirection) {
		return this.createCubeSortDefinition(new ScriptExpression(filterExpr), targetLevel, axisQulifierLevel,
				axisQulifierValue, sortDirection);
	}

	/**
	 * Create a new ILevelDefinition instance.
	 * 
	 * @param dimensionName
	 * @param hierarchyName
	 * @param levelName
	 * @return
	 */
	public ILevelDefinition createLevel(String dimensionName, String hierarchyName, String levelName) {
		return null;
	}

	public ICubeOperationFactory getCubeOperationFactory() {
		return CubeOperationFactory.getInstance();
	}

	public ICubeSortDefinition createCubeSortDefinition(IScriptExpression filterExpr, ILevelDefinition targetLevel,
			ILevelDefinition[] axisQulifierLevel, Object[] axisQulifierValue, int sortDirection) {
		CubeSortDefinition cubeSortDefn = new CubeSortDefinition();
		cubeSortDefn.setExpression(filterExpr);
		cubeSortDefn.setTargetLevel(targetLevel);
		cubeSortDefn.setAxisQualifierLevels(axisQulifierLevel);
		cubeSortDefn.setAxisQualifierValues(axisQulifierValue);
		cubeSortDefn.setSortDirection(sortDirection);
		return cubeSortDefn;
	}

}
