/*
 * #%L
 * SciJava UI components for Java Swing.
 * %%
 * Copyright (C) 2010 - 2017 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.ui.swing.widget;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.scijava.log.LogService;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.util.GenericUtils;
import org.scijava.util.Range;
import org.scijava.widget.InputWidget;
import org.scijava.widget.WidgetModel;

/**
 * Swing implementation of range selection widget.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = InputWidget.class)
public class SwingNumberRangeWidget<N extends Number & Comparable<N>> extends
	SwingInputWidget<Range<N>> implements ChangeListener
{

	@Parameter
	private ThreadService threadService;

	@Parameter
	private ModuleService moduleService;

	@Parameter
	private LogService log;

	private RangeSlider slider;

	// -- InputWidget methods --

	@Override
	public Range<N> getValue() {
		Range.between(fromInclusive, toInclusive)
		return new Range(slider.getMinimum(), slider.getMaximum());
	}

	// -- WrapperPlugin methods --

	@Override
	public void set(final WidgetModel model) {
		super.set(model);

		final Number min = 1;//model.getMin();
		final Number max = 10;//model.getMax();
		final Number softMin = 3;//model.getSoftMin();
		final Number softMax = 7;//model.getSoftMax();

		System.out.println("====> min = " + min + ", max = " + max);
		slider = new RangeSlider(min.intValue(), max.intValue());
		slider.setValue(softMin.intValue());
		slider.setUpperValue(softMax.intValue());
		slider.addChangeListener(this);
		setToolTip(slider);
		getComponent().add(slider);

		refreshWidget();
	}

	// -- Typed methods --

	@Override
	public boolean supports(final WidgetModel model) {
		return super.supports(model) && isNumberRange(model);
	}

	// -- ChangeListener methods --

	@Override
	public void stateChanged(final ChangeEvent e) {
		updateModel();
	}
	
	// -- AbstractUIInputWidget methods ---

	@Override
	public void doRefresh() {
		final Object value = get().getValue();
		if (!(value instanceof Range)) return; // invalid value
		final Range<? extends Number> range = (Range<?>) value;
		if (slider.getValue() != range.min()) {
			System.out.println("==> slider min -> " + range.min());
			slider.setValue(range.min());
		}
		if (slider.getUpperValue() != range.max()) {
			System.out.println("==> slider max -> " + range.max());
			slider.setUpperValue(range.max());
		}
	}
	
	// -- Helper methods --

	private boolean isNumberRange(final WidgetModel model) {
		final Type rangeType = rangeType(model);
		return rangeType != null && //
			isAssignable(rangeType, Number.class) && //
			isAssignable(rangeType, Comparable.class);
	}

	private Class<?> rangeType(final WidgetModel model) {
		final Type type = model.getItem().getGenericType();
		final Type rangeType = GenericUtils.getTypeParameter(type, Range.class, 0);
		return GenericUtils.getClasses(rangeType).stream() //
			.filter(c -> ) //
			.findFirst();
	}

	private Range<N> createRange(N min, N max) {
		return Range.between(min, max);
	}

	private Range<N> asNumberRange(final Object value) {
		if (!(value instanceof Range)) return null;
		@SuppressWarnings("unchecked")
		final Range<? extends Number> numberRange = (Range<? extends Number>) value;
		return numberRange;
	}
}