package data;

import java.util.HashSet;

public class FeatureGeneratorInput extends EvaluationInput {

	HashSet<String> processedInput;

	public FeatureGeneratorInput() {
		this.processedInput = new HashSet<String>();
	}

	private String getHash(String authorId, int proceedingId)
	{
		return authorId + proceedingId;
	}

	public void addProcessedInput(String authorId, int proceedingId)
	{
		processedInput.add(this.getHash(authorId, proceedingId));
	}

	public boolean isProcessed(String authorId, int proceedingId)
	{
		return processedInput.contains(this.getHash(authorId, proceedingId));
	}

	@Override
	public void addEntry(String authorId, Integer proceedingId) {
		// TODO Auto-generated method stub
		if(!this.isProcessed(authorId, proceedingId))
		{
			super.addEntry(authorId, proceedingId);
		}
		else
		{
			System.out.println("AuthorId, ProceedingId alreadyProcessed-" + authorId + ", " + proceedingId);
		}
	}
}
