const SUBSCRIPTION_PLANS = {
  FREE: {
    maxGiveaways: 3,
    maxWinners: 2,
    maxComments: 300,
  },
  GOLD: {
    maxGiveaways: 10,
    maxWinners: 5,
    maxComments: 600,
  },
  DIAMOND: {
    maxGiveaways: -1, // -1 indicates unlimited
    maxWinners: 10,
    maxComments: 1000,
  },
};

export default SUBSCRIPTION_PLANS;
