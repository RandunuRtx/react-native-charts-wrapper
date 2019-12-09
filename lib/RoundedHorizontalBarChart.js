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

class RoundedHorizontalBarChart extends React.Component {
  getNativeComponentName() {
    return 'RNRoundedHorizontalBarChart'
  }

  getNativeComponentRef() {
    return this.nativeComponentRef
  }

  render() {
    return <RNRoundedHorizontalBarChart {...this.props} ref={ref => this.nativeComponentRef = ref} />;
  }

}

RoundedHorizontalBarChart.propTypes = {
  ...BarLineChartBase.propTypes,

  drawValueAboveBar: PropTypes.bool,
  drawBarShadow: PropTypes.bool,
  highlightFullBarEnabled: PropTypes.bool,

  data: barData
};

var RNRoundedHorizontalBarChart = requireNativeComponent('RNRoundedHorizontalBarChart', RoundedHorizontalBarChart, {
  nativeOnly: {onSelect: true, onChange: true}
});

export default HighlightEnhancer(ScaleEnhancer(MoveEnhancer(RoundedHorizontalBarChart)))