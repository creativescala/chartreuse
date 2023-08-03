package chartreuse

enum MajorTickLayout {
  case Manual(ticks: Seq[Double])
  case Algorithmic(tickCount: Int)
  case NoTicks
}

enum MinorTickLayout {
  case Algorithmic(tickCount: Int)
  case NoTicks
}
