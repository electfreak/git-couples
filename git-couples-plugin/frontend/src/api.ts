type branches = string[];

interface Developer {
    email: string;
    names: string[];
    login: string | null;
}

interface ContributionToFile {
  filePath: string;
  fileChangeCommitCountMap: number;
}

interface CommonPairContribution {
  devA: Developer;
  devB: Developer;
  intersectedContribution: ContributionToFile[];
}
