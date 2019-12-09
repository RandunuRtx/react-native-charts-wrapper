import PropTypes from 'prop-types';
import React, {Component} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import BarLineChartBase from './BarLineChartBase';
import {barData} from './ChartDataConfig';
import MoveEnhancer from './MoveEnhancer'
import ScaleEnhancer from "./ScaleEnhancer";
import HighlightEnhancer from "./HighlightEnhancer";
import ScrollEnhancer from "./ScrollEnhancer";
import BarChart from 'react-native-charts-wrapper/lib/BarChart';

class RoundedBarChart extends BarChart {
  getNativeComponentName() {
    return 'RNRoundedBarChart'
  }

  getNativeComponentRef() {
    return this.nativeComponentRef
  }

  render() {
    return <RNRoundedBarChart {...this.props} ref={ref => this.nativeComponentRef = ref} />;
  }
}

RoundedBarChart.propTypes = {
  ...BarLineChartBase.propTypes,

  drawValueAboveBar: PropTypes.bool,
  drawBarShadow: PropTypes.bool,
  highlightFullBarEnabled: PropTypes.bool,

  data: barData
}

var RNRoundedBarChart = requireNativeComponent('RNRoundedBarChart', RoundedBarChart, {
  nativeOnly: {onSelect: true, onChange: true}
})

export default ScrollEnhancer(HighlightEnhancer(ScaleEnhancer(MoveEnhancer(RoundedBarChart))))
